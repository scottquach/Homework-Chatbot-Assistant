package com.scottquach.homeworkchatbotassistant.activities

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.fragments.AlertDialogFragment
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.fragments.CreateClassFragment
import com.scottquach.homeworkchatbotassistant.fragments.DisplayScheduleFragment
import com.scottquach.homeworkchatbotassistant.fragments.NavigationFragment
import com.scottquach.homeworkchatbotassistant.utils.AnimationUtils
import kotlinx.android.synthetic.main.activity_class_schedule.*
import kotlinx.android.synthetic.main.toolbar_main.*

class ClassScheduleActivity : AppCompatActivity(), CreateClassFragment.CreateClassInterface,
        DisplayScheduleFragment.ScheduleDisplayInterface, NavigationFragment.NavigationFragmentInterface,
        AlertDialogFragment.AlertDialogInterface {

    private var databaseReference = FirebaseDatabase.getInstance().reference
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_schedule)

        val toolbar = toolbar_main
        toolbar_title.text = getString(R.string.classes)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
//            openScheduleDisplayFragment()
            val fragment = DisplayScheduleFragment()
            supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_class, fragment, canGoBack = false)
        }

        toolbar_menu_icon.setOnClickListener {
            openNavigation()
        }

        activity_container_class.setOnTouchListener(object : SwipeGestureListener(this) {
            override fun onSwipeRight() {
                val fragment = supportFragmentManager.findFragmentByTag(NavigationFragment::class.java.name)
                if (fragment == null || !fragment.isVisible) {
                    openNavigation()
                }
            }

            override fun onSwipeLeft() {
                val fragment = supportFragmentManager.findFragmentByTag(DisplayScheduleFragment::class.java.name)
                if (fragment == null || !fragment.isVisible) {
                    openScheduleDisplayFragment()
                }
            }
        })
    }

    private fun openScheduleDisplayFragment() {
        AnimationUtils.textFade(toolbar_title, getString(R.string.classes),
                resources.getInteger(android.R.integer.config_shortAnimTime))
        AnimationUtils.fadeIn(toolbar_menu_icon, resources.getInteger(android.R.integer.config_shortAnimTime))
        val fragment = DisplayScheduleFragment()
        supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_class, fragment, false, true)
    }

    private fun openCreateClassFragment() {
        val fragment = CreateClassFragment()
        supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_class, fragment, true, true)
    }

    private fun openNavigation() {
        AnimationUtils.textFade(toolbar_title, getString(R.string.navigation),
                resources.getInteger(android.R.integer.config_shortAnimTime))
        AnimationUtils.fadeOut(toolbar_menu_icon, resources.getInteger(android.R.integer.config_shortAnimTime))
        val fragment = NavigationFragment()
        supportFragmentManager.changeFragmentLeftAnimated(R.id.fragment_container_class, fragment, false, true)
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
        //Needs to switch from the standard left to right since create card is from right
        val fragment = DisplayScheduleFragment()
        supportFragmentManager.changeFragmentLeftAnimated(R.id.fragment_container_class, fragment, canGoBack = false)
    }

    override fun startClassScheduleActivity() {
        openScheduleDisplayFragment()
    }

    override fun startDisplayHomeworkActivity() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val transitionName = getString(R.string.transition_tooblar)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@ClassScheduleActivity, toolbar_main, transitionName)
            startActivity(Intent(this@ClassScheduleActivity, DisplayAssignmentsActivity::class.java),
                    transitionActivityOptions.toBundle())
        } else {
            startActivity(Intent(this@ClassScheduleActivity, DisplayAssignmentsActivity::class.java))
        }
    }


    override fun startMainActivity() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val transitionName = getString(R.string.transition_tooblar)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@ClassScheduleActivity, toolbar_main, transitionName)
            startActivity(Intent(this@ClassScheduleActivity, MainActivity::class.java),
                    transitionActivityOptions.toBundle())
        } else {
            startActivity(Intent(this@ClassScheduleActivity, MainActivity::class.java))
        }
    }

    override fun notifyNoInternetConnection() {
        AlertDialogFragment.newInstance(getString(R.string.no_internet_connection),
                getString(R.string.cannot_create_class_internet_connection), positiveString = "Ok",haveNegative = false)
                .show(supportFragmentManager, AlertDialogFragment::class.java.name)
    }

    override fun onAlertPositiveClicked(dialog: Dialog){
        dialog.dismiss()
    }

    override fun onAlertNegativeClicked(dialog: Dialog) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
