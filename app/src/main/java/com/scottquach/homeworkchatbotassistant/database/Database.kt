package com.scottquach.homeworkchatbotassistant.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.BaseApplication
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import timber.log.Timber

/**
 * Created by Scott Quach on 10/19/2017.
 */
class Database {

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    private val userClasses = mutableListOf<ClassModel>()
    private val userAssignments = mutableListOf<AssignmentModel>()

    private var convoContext: String = ""
    private var classContext: String = ""

    init {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("Database could not load dataSnapshot")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                compileData(dataSnapshot)
            }
        })
    }

    fun compileData(dataSnapshot: DataSnapshot) {
        userClasses.clear()
        userAssignments.clear()

        Timber.d("Compiling data")
        for (ds in dataSnapshot.child("users").child(user!!.uid).child("assignments").children) {
            val model = AssignmentModel()
            model.title = ds.child("title").value as String
            model.dueDate = ds.child("dueDate").value as String
            model.userClass = ds.child("userClass").value as String
            model.scale = (ds.child("scale").value as Long).toInt()
            model.key = ds.child("key").value as String

            userAssignments.add(model)
        }

        for (ds in dataSnapshot.child("users").child(user.uid).child("classes").children) {
            val model = ClassModel()
            model.title = ds.child("title").value as String
            model.timeEnd = TimeModel(ds.child("timeEnd").child("timeEndHour").value as Long,
                    ds.child("timeEnd").child("timeEndMinute").value as Long)
            ds.child("days").children.mapTo(model.days) { (it.value as Long).toInt() }

            userClasses.add(model)
        }

        if (!BaseApplication.getInstance().isFirstOpen) {
            if (dataSnapshot.child("users").child(user.uid).child("contexts").child("conversation").value != null) {
                convoContext = dataSnapshot.child("users").child(user.uid).child("contexts").child("conversation").value as String
            }
            if (dataSnapshot.child("users").child(user.uid).child("contexts").child("class").value != null) {
                classContext = dataSnapshot.child("users").child(user.uid).child("contexts").child("class").value as String
            }
        }
    }

    /**
     * Returns a copy of user assignments
     */
    fun getAssignments(): List<AssignmentModel> {
        val copy = mutableListOf<AssignmentModel>()
        copy.addAll(userAssignments)
        return copy
    }

    /**
     * Returns a copy of user classes
     */
    fun getClasses(): List<ClassModel> {
        val copy = mutableListOf<ClassModel>()
        copy.addAll(userClasses)
        return copy
    }

    fun getConvoContext() = convoContext
    fun getClassContext() = classContext
}