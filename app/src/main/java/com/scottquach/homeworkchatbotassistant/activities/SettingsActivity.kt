package com.scottquach.homeworkchatbotassistant.activities

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.*
import com.scottquach.homeworkchatbotassistant.BaseApplication
import com.scottquach.homeworkchatbotassistant.Constants

import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.contracts.SettingsContract
import com.scottquach.homeworkchatbotassistant.presenters.SettingsPresenter
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_main.view.*
import com.scottquach.homeworkchatbotassistant.jobs.JobNotifyAssignmentDue

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

        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, resources.getStringArray(R.array.libraries_git))
        list_libraries.adapter = adapter

        presenter = SettingsPresenter(this)

        button_sign_out.setOnClickListener {
            presenter.signOutUser()
        }

        button_resync.setOnClickListener {
            presenter.resyncAlarms()
        }

        toolbar_settings.toolbar_menu_icon.setOnClickListener {
            presenter.onBackButton()
        }

        switch_calendar.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    BaseApplication.getInstance().sharePref.edit().putBoolean(Constants.ADD_ASSIGNMENTS_TO_CALENDAR, true).apply()
                } else {
                    BaseApplication.getInstance().sharePref.edit().putBoolean(Constants.ADD_ASSIGNMENTS_TO_CALENDAR, false).apply()
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        switch_calendar.isChecked = BaseApplication.getInstance().sharePref.getBoolean(Constants.ADD_ASSIGNMENTS_TO_CALENDAR, true)

    }
}
