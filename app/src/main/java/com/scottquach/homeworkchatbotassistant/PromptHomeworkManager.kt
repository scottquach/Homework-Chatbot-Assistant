package com.scottquach.homeworkchatbotassistant

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

    init {
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.d("onDataChange called from homework manager")
                loadData(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.toString())
            }
        })
    }

    fun determineNextAlarm() {
        val calendar = Calendar.getInstance()
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val classesOnDay = getNextClassesByDay(currentDay)
        val nextClass = getNextClassOfDay(classesOnDay, TimeModel(10, 11))


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
        Timber.d(userClasses.toString())
        determineNextAlarm()
    }

    /**
     * Returns a list of class models that occur on a specific day
     *
     * @param Day of the week as an Int
     */
    private fun getNextClassesByDay(specifiedDay: Int): List<ClassModel> {
        var classesOnDay = userClasses
                .filter { it.days.contains(specifiedDay) }
        Timber.d(classesOnDay.toString())
        return classesOnDay
    }

    private fun getNextClassOfDay(classesOnDay: List<ClassModel>, currentTime: TimeModel): ClassModel {
        Timber.d("current time is" + currentTime.timeEndHour + " " + currentTime.timeEndMinute)
        Timber.d("original list " + classesOnDay.toString())
        var nextClasses = classesOnDay
                .filter {
                    it.timeEnd.timeEndHour >= currentTime.timeEndHour &&
                            it.timeEnd.timeEndMinute >= currentTime.timeEndMinute
                }
                .sortedBy { it.timeEnd.timeEndHour }
        Timber.d(nextClasses.toString())
        if (nextClasses.isEmpty()) {
            Timber.d("classes was empty")
            Timber.d(classesOnDay.toString())
            nextClasses = classesOnDay
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
            Timber.d("single result" + nextClasses[0].toString()  )
            nextClasses[0]
        }
    }


    private fun startNextAlarm() {
        var intent = Intent(context, PromptHomeworkReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    }

}
