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
import timber.log.Timber
import java.sql.Timestamp
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
        val currentTime = Timestamp(System.currentTimeMillis())
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val classesOnDay = getNextClassesByDay(currentDay)

        val nextClass = getNextClassOfDay(classesOnDay)


    }

    /**
     * loads the users classes from the Firebase Database and stores them in a mutableList
     */
    private fun loadData(dataSnapshot: DataSnapshot) {
        for (ds in dataSnapshot.child("users").child(user?.uid).child("classes").children) {
            var model = ClassModel()
            model.title = ds.child("title").value as String
            model.timeStart = Timestamp(ds.child("timeEnd").child("time").value as Long)
            ds.child("days").children.mapTo(model.days) { (it.value as Long).toInt()}
            userClasses.add(model)
        }
    }

    /**
     * Returns a list of class models that occur on a specific day
     *
     * @param Day of the week as an Int
     */
    private fun getNextClassesByDay(specifiedDay:Int): MutableList<ClassModel> {
        var classesOnDay = mutableListOf<ClassModel>()
        for(model in userClasses) {
            if (model.days.contains(specifiedDay)) {
                classesOnDay.add(model)
                break
            }
        }
        return classesOnDay
    }

    private fun getNextClassOfDay(classesOnDay: MutableList<ClassModel>): ClassModel {


        return ClassModel()
    }




    private fun startNextAlarm() {
        var intent = Intent(context, PromptHomeworkReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    }

}
