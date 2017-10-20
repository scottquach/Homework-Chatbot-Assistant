package com.scottquach.homeworkchatbotassistant

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import timber.log.Timber

/**
 * Created by Scott Quach on 10/19/2017.
 */
class Database {

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    init {
        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("Database could not load dataSnapshot")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                compileData(dataSnapshot)
            }
        })
    }

    fun compileData(dataSnapshot: DataSnapshot) {
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


    }

    companion object {

        private val userClasses = mutableListOf<ClassModel>()
        private val userAssignments = mutableListOf<AssignmentModel>()

        fun getAssignments(): List<AssignmentModel> {
            return userAssignments.toList()
        }

//        fun getClasses(): List<ClassModel> {
//
//        }
    }


}