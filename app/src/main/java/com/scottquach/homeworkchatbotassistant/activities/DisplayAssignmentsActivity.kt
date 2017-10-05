package com.scottquach.homeworkchatbotassistant.activities

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
        toolbar_menu_icon.visibility = View.INVISIBLE

        val fragment = NavigationFragment()
        supportFragmentManager.changeFragmentLeftAnimated(R.id.fragment_container_homework, fragment)
    }

    override fun startClassScheduleActivity() {
        startActivity(Intent(this@DisplayAssignmentsActivity, ClassScheduleActivity::class.java))
    }

    override fun startDisplayHomeworkActivity() {
        toolbar_title.text = getString(R.string.assignments)
        toolbar_menu_icon.visibility = View.VISIBLE

        val fragment = DisplayAssignmentsFragment()
        supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_homework, fragment, false, true)
    }

    override fun startMainActivity() {
        startActivity(Intent(this@DisplayAssignmentsActivity, MainActivity::class.java))
    }
}
