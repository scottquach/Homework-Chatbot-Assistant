package com.scottquach.homeworkchatbotassistant.activities

import android.app.ActivityOptions
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.scottquach.homeworkchatbotassistant.fragments.NavigationFragment
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.changeFragment
import com.scottquach.homeworkchatbotassistant.changeFragmentLeftAnimated
import com.scottquach.homeworkchatbotassistant.changeFragmentRightAnimated
import com.scottquach.homeworkchatbotassistant.fragments.DisplayAssignmentsFragment
import com.scottquach.homeworkchatbotassistant.utils.AnimationUtils
import kotlinx.android.synthetic.main.toolbar_main.*

class DisplayAssignmentsActivity : AppCompatActivity(), NavigationFragment.NavigationFragmentInterface,
        DisplayAssignmentsFragment.DisplayHomeworkInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_homework)

        val toolbar = toolbar_main
        setSupportActionBar(toolbar)
        toolbar_title.text = "Assignments"
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
            val fragment = DisplayAssignmentsFragment()
            supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_homework,
                    fragment, false, false)
        }

        toolbar_menu_icon.setOnClickListener {
            openNavigation()
        }
    }

    private fun openNavigation() {
        toolbar_title.text = getString(R.string.navigation)
        AnimationUtils.textFade(toolbar_title, getString(R.string.navigation),
                resources.getInteger(android.R.integer.config_shortAnimTime))
        AnimationUtils.fadeOut(toolbar_menu_icon, resources.getInteger(android.R.integer.config_shortAnimTime))

        val fragment = NavigationFragment()
        supportFragmentManager.changeFragmentLeftAnimated(R.id.fragment_container_homework, fragment)
    }

    override fun startClassScheduleActivity() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val transitionName = getString(R.string.transition_tooblar)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@DisplayAssignmentsActivity, toolbar_main, transitionName)
            startActivity(Intent(this@DisplayAssignmentsActivity, ClassScheduleActivity::class.java),
                    transitionActivityOptions.toBundle())
        } else {
            startActivity(Intent(this@DisplayAssignmentsActivity, ClassScheduleActivity::class.java))
        }
        }

    override fun startDisplayHomeworkActivity() {
        AnimationUtils.textFade(toolbar_title, getString(R.string.assignments),
                resources.getInteger(android.R.integer.config_shortAnimTime))
        AnimationUtils.fadeIn(toolbar_menu_icon, resources.getInteger(android.R.integer.config_shortAnimTime))

        val fragment = DisplayAssignmentsFragment()
        supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_homework, fragment, false, true)
    }

    override fun startMainActivity() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val transitionName = getString(R.string.transition_tooblar)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@DisplayAssignmentsActivity, toolbar_main, transitionName)
            startActivity(Intent(this@DisplayAssignmentsActivity, MainActivity::class.java),
                    transitionActivityOptions.toBundle())
        } else {
            startActivity(Intent(this@DisplayAssignmentsActivity, MainActivity::class.java))
        }
    }
}
