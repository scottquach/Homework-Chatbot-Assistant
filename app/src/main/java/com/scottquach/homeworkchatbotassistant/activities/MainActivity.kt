package com.scottquach.homeworkchatbotassistant.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scottquach.homeworkchatbotassistant.BaseApplication
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.database.MessageDatabaseHandler
import com.scottquach.homeworkchatbotassistant.R

import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.scottquach.homeworkchatbotassistant.fragments.*
import timber.log.Timber

import com.scottquach.homeworkchatbotassistant.utils.AnimationUtils
import com.scottquach.homeworkchatbotassistant.utils.InstrumentationUtils
import kotlinx.android.synthetic.main.toolbar_main.view.*


class MainActivity : AppCompatActivity(), DisplayScheduleFragment.ScheduleDisplayInterface
        , AlertDialogFragment.AlertDialogInterface, CreateClassFragment.CreateClassInterface {


    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    private lateinit var drawer: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar_main) as Toolbar
        val toolbarTitle = findViewById<View>(R.id.toolbar_title) as TextView
        toolbarTitle.text = getString(R.string.chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        createDrawer()

        if (BaseApplication.getInstance().isFirstOpen) {
            Timber.d("first open")
            val handler = MessageDatabaseHandler(this, this)
            handler.receiveWelcomeMessages()
            BaseApplication.getInstance().sharePref.edit().putBoolean("first_open", false).apply()
        } else {
            Timber.d("Wasn't first open")
        }

        if (savedInstanceState == null) {
            val fragment = ChatFragment()
            supportFragmentManager.changeFragmentRightAnimated(
                    R.id.fragment_container_main, fragment, false, false)
        }
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), 0)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR), 0)
        }
    }

    private fun createDrawer() {
        val toolbar = findViewById<View>(R.id.toolbar_main) as Toolbar

        val classesItem = PrimaryDrawerItem().withIdentifier(1).withName(R.string.classes)
                .withIcon(R.drawable.ic_class).withIconColor(resources.getColor(R.color.darkGrey))
                .withIconTintingEnabled(true)
                .withSelectedColor(resources.getColor(R.color.lightGrey))
        val assignmentsItem = PrimaryDrawerItem().withIdentifier(2).withName(R.string.assignments)
                .withIcon(R.drawable.ic_homework_simple).withIconColor(resources.getColor(R.color.darkGrey))
                .withIconTintingEnabled(true)
                .withSelectedColor(resources.getColor(R.color.lightGrey))
        val chatItem = PrimaryDrawerItem().withIdentifier(3).withName(R.string.chat)
                .withIcon(R.drawable.ic_chat).withIconColor(resources.getColor(R.color.darkGrey))
                .withIconTintingEnabled(true)
                .withSelectedColor(resources.getColor(R.color.lightGrey))
        val settingsItem = SecondaryDrawerItem().withIdentifier(4).withName(R.string.settings)
                .withIcon(R.drawable.ic_settings).withIconColor(resources.getColor(R.color.darkGrey))
                .withIconTintingEnabled(true)
                .withSelectedColor(resources.getColor(R.color.lightGrey))
        val feedbackItem = SecondaryDrawerItem().withIdentifier(5).withName(getString(R.string.feedback))
                .withIcon(R.drawable.ic_feedback).withIconColor(resources.getColor(R.color.darkGrey))
                .withIconTintingEnabled(true)
                .withSelectedColor(resources.getColor(R.color.lightGrey))
                .withSelectable(false)

        val header = AccountHeaderBuilder().withActivity(this)
                .withHeaderBackground(R.drawable.background_gradient_blue)
                .addProfiles(
                        ProfileDrawerItem().withName(R.string.app_name).withIcon(R.mipmap.ic_launcher)
                )
                .withPaddingBelowHeader(true)
                .withSelectionListEnabled(false)
                .build()

        drawer = DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withSliderBackgroundColor(resources.getColor(R.color.darkWhite))
                .withAccountHeader(header)
                .withCloseOnClick(true)
                .addDrawerItems(
                        classesItem,
                        DividerDrawerItem(),
                        assignmentsItem,
                        DividerDrawerItem(),
                        chatItem,
                        DividerDrawerItem(),
                        settingsItem,
                        feedbackItem
                )
                .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
                        when (drawerItem) {
                            classesItem -> {
                                val fragment = DisplayScheduleFragment()
                                supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_main, fragment, canGoBack = false)
                                AnimationUtils.textFade(toolbar.toolbar_title, getString(R.string.classes),
                                        resources.getInteger(android.R.integer.config_shortAnimTime))
                                closeDrawer()
                            }
                            assignmentsItem -> {
                                val fragment = DisplayAssignmentsFragment()
                                supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_main,
                                        fragment, false, false)
                                AnimationUtils.textFade(toolbar.toolbar_title, getString(R.string.assignments),
                                        resources.getInteger(android.R.integer.config_shortAnimTime))
                                closeDrawer()
                            }
                            chatItem -> {
                                val fragment = ChatFragment()
                                supportFragmentManager.changeFragmentRightAnimated(
                                        R.id.fragment_container_main, fragment, false, false)
                                AnimationUtils.textFade(toolbar.toolbar_title, getString(R.string.chat),
                                        resources.getInteger(android.R.integer.config_shortAnimTime))
                                closeDrawer()
                            }
                            settingsItem -> {
                                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                            }
                            feedbackItem -> {
                                EmailHandler(this@MainActivity).sendFeedbackEmail()
                                logEvent(InstrumentationUtils.SEND_FEEDBACK)
                            }
                        }
                        return true
                    }

                })
                .build()

        drawer.deselect()
    }

    fun closeDrawer() {
        drawer.closeDrawer()
        val view = this.getCurrentFocus()
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    override fun switchToDisplayFragment() {
        val fragment = DisplayScheduleFragment()
        supportFragmentManager.changeFragmentLeftAnimated(R.id.fragment_container_main, fragment, canGoBack = false)
    }


    override fun switchToCreateFragment() {
        val fragment = CreateClassFragment()
        supportFragmentManager.changeFragmentRightAnimated(R.id.fragment_container_main, fragment, true, true)
    }

    override fun notifyNoInternetConnection() {
        AlertDialogFragment.newInstance(getString(R.string.no_internet_connection),
                getString(R.string.cannot_send_messages_internet_connection), positiveString = getString(R.string.ok), haveNegative = false)
                .show(supportFragmentManager, AlertDialogFragment::class.java.name)
    }

    override fun onAlertPositiveClicked(dialog: Dialog) {
        dialog.dismiss()
    }

    override fun onAlertNegativeClicked(dialog: Dialog) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
