package com.scottquach.homeworkchatbotassistant

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import timber.log.Timber
import java.util.*

/**
 * Created by Scott Quach on 9/16/2017.
 */

class PromptHomeworkManager(var context: Context) {

    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var userClasses: MutableList<ClassModel> = mutableListOf()

    private var daysFromNow: Int = 0

    fun startManaging() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.d("onDataChange called from homework manager")
                loadData(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.toString())
            }
        })
    }

    private fun determineNextAlarm() {
        val calendar = Calendar.getInstance()
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val classesOnDay = getNextClassesByDay(currentDay)
        Timber.d("current day is " + currentDay)
        var nextClass = getNextClassOfDay(classesOnDay, TimeModel(calendar.get(Calendar.HOUR_OF_DAY).toLong(), calendar.get(Calendar.MINUTE).toLong()))
        startNextAlarm(nextClass)
    }

    /**
     * loads the users classes from the Firebase Database and stores them in a mutableList
     */
    private fun loadData(dataSnapshot: DataSnapshot) {
        for (ds in dataSnapshot.child("users").child(user?.uid).child("classes").children) {
            var model = ClassModel()
            model.title = ds.child("title").value as String
            model.timeEnd = TimeModel(ds.child("timeEnd").child("timeEndHour").value as Long,
                    ds.child("timeEnd").child("timeEndMinute").value as Long)
            ds.child("days").children.mapTo(model.days) { (it.value as Long).toInt() }
            userClasses.add(model)
        }
        Timber.d("original classes" + userClasses.toString())
        determineNextAlarm()
    }

    /**
     * Returns a list of class models that occur on a specific day
     *
     * @param Day of the week as an Int, occursOnDay - set false if you want to get a class
     * that occurs after the specified day(such as a weekend from Friday to Monday)
     */
    private fun getNextClassesByDay(specifiedDay: Int, occursOnDay: Boolean = true): MutableList<ClassModel> {
        var iteratedDay = specifiedDay
        //Make sure days from now is reset
        daysFromNow = 0
        var classesOnDay = emptyList<ClassModel>().toMutableList()
        Timber.d("iterated day is" + iteratedDay)
        do {
            if (iteratedDay < 7 && !occursOnDay) {
                iteratedDay++
            } else if (!occursOnDay) {
                iteratedDay = 1
            }
            daysFromNow++
            classesOnDay = userClasses
                    .filter { it.days.contains(iteratedDay) }
                    .toMutableList()

        } while (classesOnDay.isEmpty())
        Timber.d("iterated day new " + iteratedDay)
        Timber.d(classesOnDay.toString())
        return classesOnDay
    }

    /**
     * Determines the next class of the day
     *
     * @param List of classes for the day, current time that returned class returns after
     */
    private fun getNextClassOfDay(classesOnDay: List<ClassModel>, currentTime: TimeModel): ClassModel {
        Timber.d("original of classes on day " + classesOnDay.toString())
        var nextClasses = classesOnDay
                .filter {
                    (it.timeEnd.timeEndHour > currentTime.timeEndHour) ||
                            (it.timeEnd.timeEndHour == currentTime.timeEndHour && it.timeEnd.timeEndMinute > currentTime.timeEndMinute)
                }
                .sortedBy { it.timeEnd.timeEndHour }
        Timber.d(nextClasses.toString())
        if (nextClasses.isEmpty()) {
            Timber.d("classes was empty, occurs on another day")
            Timber.d(classesOnDay.toString())
            //Re-load class of the day, except day is automatically the next day that a class occurs
            var classesNextDay = getNextClassesByDay((Calendar.getInstance().get(Calendar.DAY_OF_WEEK)), false)
            nextClasses = classesNextDay
                    .sortedBy { it.timeEnd.timeEndHour }
        }
        Timber.d("hour organized" + nextClasses.toString())
        return if (nextClasses.size > 1 && nextClasses[0].timeEnd.timeEndHour == nextClasses[1].timeEnd.timeEndHour) {
            var nextClassesByHour = nextClasses
                    .filter { it.timeEnd.timeEndHour == nextClasses[0].timeEnd.timeEndHour }
                    .sortedBy { it.timeEnd.timeEndMinute }
            Timber.d(nextClassesByHour.toString())
            nextClassesByHour[0]
        } else {
            Timber.d("single result" + nextClasses[0].toString())
            nextClasses[0]
        }
    }

    private fun isAlarmBeforeNow(alarm: Calendar): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return alarm.before(calendar)
    }

    /**
     * Initializes the next alarm. Adds time if the next class occurs multiple days in the future
     *
     * @param model of the class that the alarm should be a notifier of
     */
    private fun startNextAlarm(model: ClassModel) {
        var alarm = Calendar.getInstance()
        alarm.set(Calendar.HOUR_OF_DAY, model.timeEnd.timeEndHour.toInt())
        alarm.set(Calendar.MINUTE, model.timeEnd.timeEndMinute.toInt())

        if (isAlarmBeforeNow(alarm)) {
            Timber.d("Alarm is before")
            alarm.add(Calendar.DAY_OF_MONTH, daysFromNow)
        } else {
            Timber.d("alarm is after now")
        }

        Timber.d("added days is $daysFromNow selected class is $model")

        var intent = Intent(context, PromptHomeworkReceiver::class.java)
        intent.putExtra("class_name", model.title)
        var pendingIntent = PendingIntent.getBroadcast(context, 10, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pendingIntent)
    }
}
