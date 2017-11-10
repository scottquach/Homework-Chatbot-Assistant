package com.scottquach.homeworkchatbotassistant.activities

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Button
import android.widget.Toast

import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.contracts.SettingsContract
import com.scottquach.homeworkchatbotassistant.jobs.JobNotifyAssignmentDue
import com.scottquach.homeworkchatbotassistant.jobs.JobNotifyClassEnd
import com.scottquach.homeworkchatbotassistant.presenters.SettingsPresenter
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_main.view.*

class SettingsActivity : AppCompatActivity(), SettingsContract.View {
    override fun navigateBack() {
        finish()
    }

    override fun navigateToSignInActivity() {
        startActivity(Intent(this@SettingsActivity, SignInActivity::class.java))
    }

    override fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private lateinit var presenter: SettingsPresenter

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar_settings.toolbar_title.text = getString(R.string.settings)
        toolbar_settings.toolbar_menu_icon.visibility = View.VISIBLE
        toolbar_settings.toolbar_menu_icon.setImageResource(R.drawable.ic_arrow_back)



        presenter = SettingsPresenter(this)

        val signOutButton = findViewById<View>(R.id.button_sign_out) as Button
        signOutButton.setOnClickListener {
            presenter.signOutUser()
        }

        button_resync.setOnClickListener {
            presenter.resyncAlarms()
        }

        toolbar_settings.toolbar_menu_icon.setOnClickListener {
            presenter.onBackButton()
        }

//        val testButton = findViewById<View>(R.id.button_test) as Button
//        testButton.setOnClickListener {
//            JobSchedulerUtil.cancelAllJobs(this@SettingsActivity)
//        }
    }
}
