package com.scottquach.homeworkchatbotassistant.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.fragments.CreateClassFragment
import com.scottquach.homeworkchatbotassistant.fragments.DisplayScheduleFragment
import com.scottquach.homeworkchatbotassistant.fragments.NavigationFragment
import kotlinx.android.synthetic.main.toolbar_main.*

class ClassScheduleActivity : AppCompatActivity(), CreateClassFragment.CreateClassInterface,
    DisplayScheduleFragment.ScheduleDisplayInterface, NavigationFragment.NavigationFragmentInterface{

    private var databaseReference = FirebaseDatabase.getInstance().reference
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_schedule)

        val toolbar = toolbar_main
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        toolbar_menu_icon.setOnClickListener {
            openNavigation()
        }
    }

    override fun onResume() {
        super.onResume()
        val fragment = DisplayScheduleFragment()
        supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_class, fragment)
    }

    private fun openScheduleDisplayFragment() {
        val fragment = DisplayScheduleFragment()
        supportFragmentManager.changeFragmentLeftAnimated(R.id.fragment_container_class, fragment)
    }

    private fun openCreateClassFragment() {
        val fragment = CreateClassFragment()
        supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_class, fragment)
    }

    private fun openNavigation() {
        val fragment = NavigationFragment()
        supportFragmentManager.changeFragmentLeftAnimated(R.id.fragment_container_class, fragment)
    }

    override fun addClass(newClass: ClassModel) {
        user?.let {
            databaseReference.child("users").child(user?.uid).child("classes").child(newClass.title).setValue(newClass)
        }
        val manager = NotifyClassEndManager(this@ClassScheduleActivity)
        manager.startManaging()
    }

    override fun switchToCreateFragment() {
        openCreateClassFragment()
    }

    override fun switchToDisplayFragment() {
        openScheduleDisplayFragment()
    }

    override fun startClassScheduleActivity() {
        openScheduleDisplayFragment()
    }

    override fun startDisplayHomeworkActivity() {
        startActivity(Intent(this@ClassScheduleActivity, DisplayAssignmentsActivity::class.java))
    }

    override fun startMainActivity() {
        startActivity(Intent(this@ClassScheduleActivity, MainActivity::class.java))
    }
}
