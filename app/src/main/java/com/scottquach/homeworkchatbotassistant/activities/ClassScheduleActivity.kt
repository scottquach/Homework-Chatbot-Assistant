package com.scottquach.homeworkchatbotassistant.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import timber.log.Timber

class ClassScheduleActivity : FragmentActivity(), CreateClassFragment.CreateClassInterface,
    DisplayScheduleFragment.ScheduleDisplayListener{

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
}
