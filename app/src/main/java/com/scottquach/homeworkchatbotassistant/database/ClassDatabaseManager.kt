package com.scottquach.homeworkchatbotassistant.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import timber.log.Timber

/**
 * Created by Scott Quach on 11/2/2017.
 */
class ClassDatabaseManager(val manager:NotifyClassEndManager ) : BaseDatabase() {

    private val userClasses = mutableListOf<ClassModel>()

    fun loadData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("Database could not load dataSnapshot")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.child("users").child(user!!.uid).child("classes").children) {
                    val model = ClassModel()
                    model.title = ds.child("title").value as String
                    model.timeEnd = TimeModel(ds.child("timeEnd").child("timeEndHour").value as Long,
                            ds.child("timeEnd").child("timeEndMinute").value as Long)
                    ds.child("days").children.mapTo(model.days) { (it.value as Long).toInt() }

                    userClasses.add(model)
                }
            }
        })
    }

}