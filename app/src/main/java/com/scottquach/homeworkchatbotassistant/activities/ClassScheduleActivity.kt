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

class ClassScheduleActivity : FragmentActivity(), CreateClassFragment.CreateClassInterface,
    DisplayScheduleFragment.ScheduleDisplayListener{

    private lateinit var databaseReference: DatabaseReference
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_schedule)

        databaseReference = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser
    }

    override fun onResume() {
        super.onResume()
        openScheduleDisplayFragment()
    }

    private fun openScheduleDisplayFragment() {
        val fragment = DisplayScheduleFragment()
        supportFragmentManager.changeFragment(R.id.fragment_container_class, fragment, false)
    }

    private fun openCreateClassFragment() {
        val fragment = CreateClassFragment()
        supportFragmentManager.changeFragment(R.id.fragment_container_class, fragment, false)
    }

    override fun addClass(newClass: ClassModel) {
        user?.let {
            databaseReference.child("users").child(user?.uid).child("classes").child(newClass.title).setValue(newClass)
        }
    }

    override fun switchToCreateFragment() {
        openCreateClassFragment()
    }

    override fun switchToDisplayFragment() {
        openScheduleDisplayFragment()
    }
}
