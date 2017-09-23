package com.scottquach.homeworkchatbotassistant.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.fragments.CreateClassFragment
import com.scottquach.homeworkchatbotassistant.fragments.DisplayScheduleFragment
import com.scottquach.homeworkchatbotassistant.fragments.NavigationFragment

class ClassScheduleActivity : FragmentActivity(), CreateClassFragment.CreateClassInterface,
    DisplayScheduleFragment.ScheduleDisplayInterface, NavigationFragment.NavigationFragmentInterface{


    private var databaseReference = FirebaseDatabase.getInstance().reference
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_schedule)
    }

    override fun onResume() {
        super.onResume()
        openScheduleDisplayFragment()
    }

    override fun onPause() {
        super.onPause()
        val manager = PromptHomeworkManager(this@ClassScheduleActivity)
        manager.startManaging()
    }

    private fun openScheduleDisplayFragment() {
        val fragment = DisplayScheduleFragment()
        val transaction = supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_enter, R.anim.slide_enter, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container_class, fragment, "fragment")
        transaction.commit()
    }

    private fun openCreateClassFragment() {
        val fragment = CreateClassFragment()
        val transaction = supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_enter, R.anim.slide_enter, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container_class, fragment, "fragment")
        transaction.commit()
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

    override fun startClassScheduleActivity() {
        val fragment = DisplayScheduleFragment()
        supportFragmentManager.changeFragment(R.id.fragment_container_class, fragment)
    }

    override fun startDisplayHomeworkActivity() {
        startActivity(Intent(this@ClassScheduleActivity, DisplayHomeworkActivity::class.java))
    }

    override fun startMainActivity() {
        startActivity(Intent(this@ClassScheduleActivity, MainActivity::class.java))
    }
}
