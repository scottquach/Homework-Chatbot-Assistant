package com.scottquach.homeworkchatbotassistant.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import timber.log.Timber

class DisplayHomeworkActivity : AppCompatActivity() {

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var userAssignments = mutableListOf<AssignmentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_homework)

        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Timber.d("Data Changed called")
                loadData(p0)
            }

            override fun onCancelled(p0: DatabaseError?) {
                Timber.d("Database Error " + p0.toString())
            }
        })
    }

    fun loadData(dataSnapshot: DataSnapshot) {
        for (ds in dataSnapshot.child("users").child(user!!.uid).child("assignments").children) {
            var model = AssignmentModel()
            model.title = ds.child("title").value as String
            model.userClass = ds.child("userClass").value as String
            model.dueDate = ds.child("dueDate").value as String
            model.scale = ds.child("scale").value as Int
            model.key = ds.child("key").value as String
            Timber.d("assignment model " + model.toString())
            userAssignments.add(model)
        }
    }
}
