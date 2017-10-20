package com.scottquach.homeworkchatbotassistant

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import com.scottquach.homeworkchatbotassistant.utils.JobSchedulerUtil
import timber.log.Timber
import java.util.*

/**
 * Created by Scott Quach on 9/16/2017.
 * Pulls down a list of user classes and determines which
 * class will end next, then sets an alarm with a broadcast
 * intent for NotifyClassEndReceiver
 */

class NotifyClassEndManager(var context: Context) {

    private lateinit var userClasses: MutableList<ClassModel>

    private var daysFromNow: Int = 0

    private var occursOnDay = true

    /**
    This is the specific end time for the class that just ended and called startManaging in order
    to schedule the job for next class. This is needed instead of System.currentTimeMillis() because
    the job scheduler has a time variation that means that this job may have not been called at the
    exact class end time but instead a few minutes before and after.
     */
    private val previousEndTime = Calendar.getInstance()

    fun startManaging(specificEndTime: Long = System.currentTimeMillis()) {
        //If this wasn't called by a end class time job, just use current time
        previousEndTime.timeInMillis = specificEndTime
        Timber.d("Previous end time is ${previousEndTime.timeInMillis}")
        userClasses = BaseApplication.getInstance().database.getClasses().toMutableList()
        determineNextAlarm()

    }

    private fun determineNextAlarm() {
        if (userClasses.isNotEmpty()) {
            val classesOnDay = getNextClassesByDay(previousEndTime.get(Calendar.DAY_OF_WEEK))
            Timber.d("current day is " + previousEndTime.get(Calendar.DAY_OF_WEEK))

            val nextClass = getNextClassOfDay(classesOnDay, TimeModel(previousEndTime.get(Calendar.HOUR_OF_DAY).toLong(),
                    previousEndTime.get(Calendar.MINUTE).toLong()))

            startNextAlarm(nextClass)
        }
    }


    /**
     * Returns a list of class models that occur on a specific day, if there are no classes on the
     * specified day, it returns classes for the next day that has a class
     *
     * @param Day of the week as an Int, occursOnDay - set false if you want to get a class
     * that occurs after the specified day(such as a weekend from Friday to Monday)
     */
    private fun getNextClassesByDay(specifiedDay: Int): MutableList<ClassModel> {
        var iteratedDay = specifiedDay
        Timber.d("iterated day $iteratedDay" + userClasses.toString())

        //Loads classes that occur on iteratedDay
        fun loadClassesOnDay(): MutableList<ClassModel> {
            return userClasses
                    .filter { it.days.contains(iteratedDay) }
                    .toMutableList()
        }

        var classesOnDay = loadClassesOnDay()
        while (classesOnDay.isEmpty()) {
            occursOnDay = false
            if (iteratedDay < 7) {
                iteratedDay++
            } else {
                iteratedDay = 1
            }
            daysFromNow++
            classesOnDay = loadClassesOnDay()
            Timber.d("iterated")
        }

        Timber.d("proecessed classes on day iterated day $iteratedDay daysFromNOw $daysFromNow " + classesOnDay.toString())
        return classesOnDay
    }

    /**
     * Determines the next class of the day
     *
     * @param List of classes for the day, current time that returned class returns after
     */
    private fun getNextClassOfDay(classesOnDay: List<ClassModel>, currentTime: TimeModel): ClassModel {
        Timber.d("original of classes on day " + classesOnDay.toString())

        //This filters class that have occur on today but time has already passed
        var nextClasses = classesOnDay
                .filter {
                    (it.timeEnd.timeEndHour > currentTime.timeEndHour) ||
                            (it.timeEnd.timeEndHour == currentTime.timeEndHour && it.timeEnd.timeEndMinute > currentTime.timeEndMinute)
                }
                .sortedBy { it.timeEnd.timeEndHour }
        Timber.d(nextClasses.toString())

        Timber.d("current time is " + currentTime)

//        This reloads the list of nextClasses for tomorrow or following days since no classes occur
//        today after current time
        if (nextClasses.isEmpty()) {
            occursOnDay = false
            daysFromNow++
            Timber.d("classes was empty, occurs on another day")
            Timber.d(classesOnDay.toString())
            //Reload class of the day, except day is tomorrow
            var classesNextDay = getNextClassesByDay(previousEndTime.get(Calendar.DAY_OF_WEEK) + 1)
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
            Timber.d("single result " + nextClasses[0].toString())
            nextClasses[0]
        }
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

        if (!occursOnDay) {
            Timber.d("Doesn't occur today adding days $daysFromNow")
            alarm.add(Calendar.DAY_OF_WEEK, daysFromNow)
        } else {
            Timber.d("Occurs on today, not adding any days")
        }

        Timber.d("added days is $daysFromNow selected class is $model")
        Timber.d(alarm.timeInMillis.toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarm.add(Calendar.MINUTE, -2)
            val minimumLatency = (alarm.timeInMillis - previousEndTime.timeInMillis) as Long
            alarm.add(Calendar.MINUTE, 4)
            val overrideDeadline = (alarm.timeInMillis - previousEndTime.timeInMillis) as Long
            alarm.add(Calendar.MINUTE, -2)

            Timber.d("was after lallipop")
            JobSchedulerUtil.scheduleClassManagerJob(context, model.title, minimumLatency, overrideDeadline,
                    alarm.timeInMillis)
        } else {
            Timber.d("Was before lollipop")
            val intent = Intent(context, NotifyClassEndReceiver::class.java)
            intent.putExtra(Constants.CLASS_NAME, model.title)
            val pendingIntent = PendingIntent.getBroadcast(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pendingIntent)
        }
    }

    /**
     * Cancels any class ending alarm that currently exists
     */
    private fun cancelAlarm() {
        var intent = Intent(context, NotifyClassEndReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
