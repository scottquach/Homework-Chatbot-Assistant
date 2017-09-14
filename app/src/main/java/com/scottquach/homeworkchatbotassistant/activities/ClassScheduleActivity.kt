package com.scottquach.homeworkchatbotassistant.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.CreateClassFragment
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.DisplayScheduleFragment
import com.scottquach.homeworkchatbotassistant.changeFragment
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import java.sql.Timestamp

class ClassScheduleActivity : FragmentActivity(), CreateClassFragment.CreateClassInterface,
    DisplayScheduleFragment.ScheduleDisplayListener{


    private lateinit var databaseReference: DatabaseReference
    private var user: FirebaseUser? = null
    private var userClasses = mutableListOf<ClassModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_schedule)

        databaseReference = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        databaseReference!!.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadData(dataSnapshot)
            }

            override fun onCancelled(error: DatabaseError?) {

            }

        })
    }

    override fun onResume() {
        super.onResume()
        openScheduleDisplayFragment()
    }

    fun loadData(dataSnapshot: DataSnapshot) {
        for (ds in dataSnapshot.child("users").child(user?.uid).child("classes").children) {
            var classModel = ClassModel()
            classModel.title = ds.child("title").getValue() as String
            classModel.timeStart = Timestamp(ds.child("timeStart").child("time").value as Long)
            classModel.timeEnd = Timestamp(ds.child("timeEnd").child("time").value as Long)
            var days = mutableListOf<Int>()
            for (ds in  dataSnapshot.child("users").child(user?.uid).child("classes").child(classModel.title).child("day").children) {
                days.add((ds.value as Long).toInt())
            }
            classModel.days = days
            userClasses.add(classModel)
        }
    }

    fun openScheduleDisplayFragment() {
        val fragment = DisplayScheduleFragment()
        supportFragmentManager.changeFragment(R.id.fragment_container_class, fragment, false)
    }

    fun openCreateClassFragment() {
        val fragment = CreateClassFragment()
        supportFragmentManager.changeFragment(R.id.fragment_container_class, fragment)
    }



    override fun addClass(newClass: ClassModel) {
        databaseReference.child("users").child(user?.uid).child("classes").child(newClass.title).setValue(newClass)
    }

    override fun switchToCreateFragment() {
        openCreateClassFragment()
    }

    override fun switchToDisplayFragment() {
        openScheduleDisplayFragment()
    }
}
