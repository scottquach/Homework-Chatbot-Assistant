package com.scottquach.homeworkchatbotassistant.activities

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.BaseApplication
import com.scottquach.homeworkchatbotassistant.Constants
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.MessageHandler
import com.scottquach.homeworkchatbotassistant.SwipeGestureListener
import com.scottquach.homeworkchatbotassistant.fragments.ChatFragment
import com.scottquach.homeworkchatbotassistant.fragments.NavigationFragment
import com.scottquach.homeworkchatbotassistant.R

import ai.api.AIListener
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.app.Dialog
import timber.log.Timber

import com.scottquach.homeworkchatbotassistant.fragments.AlertDialogFragment
import com.scottquach.homeworkchatbotassistant.utils.AnimationUtils


class MainActivity : AppCompatActivity(), AIListener, NavigationFragment.NavigationFragmentInterface,
        ChatFragment.ChatInterface, AlertDialogFragment.AlertDialogInterface {

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar_main) as Toolbar
        val toolbarTitle = findViewById<View>(R.id.toolbar_title) as TextView
        toolbarTitle.text = "Chat"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        if (BaseApplication.getInstance().isFirstOpen) {
            Timber.d("first open")
            val handler = MessageHandler(this)
            handler.receiveWelcomeMessages()
            databaseReference.child("users").child(user!!.uid).child("contexts").child("conversation")
                    .setValue(Constants.CONETEXT_DEFAULT)
            databaseReference.child("users").child(user.uid).child("contexts").child("class").setValue("default")
            BaseApplication.getInstance().sharePref.edit().putBoolean("first_open", false).apply()
        } else {
            Timber.d("Wasn't first open")
        }

        if (savedInstanceState == null) {
            val fragment = ChatFragment()
            supportFragmentManager.changeFragmentRightAnimated(
                    R.id.fragment_container_main, fragment, false, false)
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.d("Retrieved DataSnapshot")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.d("error retrieving data" + databaseError.toString())
            }
        })

        toolbar.findViewById<View>(R.id.toolbar_menu_icon).setOnClickListener { openNavigation() }

        findViewById<View>(R.id.activity_container_main).setOnTouchListener(object : SwipeGestureListener(this) {
            override fun onSwipeRight() {
                val fragment = supportFragmentManager.findFragmentByTag(NavigationFragment::class.java.name) as NavigationFragment
                if (fragment == null || !fragment.isVisible) {
                    openNavigation()
                }
            }

            override fun onSwipeLeft() {
                val fragment = supportFragmentManager.findFragmentByTag(ChatFragment::class.java.name) as ChatFragment
                if (fragment == null || !fragment.isVisible) {
                    startMainActivity()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        requestPermissions()
    }

    override fun onResult(response: AIResponse) {
        Log.d("stuff", "on response was called")
        val result = response.result
        // Get parameters
        var parameterString = ""
        if (result.parameters != null && !result.parameters.isEmpty()) {
            for ((key, value) in result.parameters) {
                parameterString += "($key, $value) "
            }
        }
    }

    override fun onError(error: AIError) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
        Timber.e(error.toString())
    }

    override fun onAudioLevel(level: Float) {

    }

    override fun onListeningStarted() {

    }

    override fun onListeningCanceled() {

    }

    override fun onListeningFinished() {

    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), 0)
        }
    }

    private fun openNavigation() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        val toolbarTitle = findViewById<View>(R.id.toolbar_title) as TextView
        AnimationUtils.textFade(toolbarTitle, getString(R.string.navigation),
                resources.getInteger(android.R.integer.config_shortAnimTime))
        val toolbarIcon = findViewById<View>(R.id.toolbar_menu_icon) as ImageView
        AnimationUtils.fadeOut(toolbarIcon, resources.getInteger(android.R.integer.config_shortAnimTime))

        val fragment = NavigationFragment()
        supportFragmentManager.changeFragmentLeftAnimated(
                R.id.fragment_container_main, fragment, true, true)
    }

    override fun startClassScheduleActivity() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val sharedView = this@MainActivity.findViewById<View>(R.id.toolbar_main)
            val transitionName = getString(R.string.transition_tooblar)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, sharedView, transitionName)
            startActivity(Intent(this@MainActivity, ClassScheduleActivity::class.java),
                    transitionActivityOptions.toBundle())
        } else {
            startActivity(Intent(this@MainActivity, ClassScheduleActivity::class.java))
        }
    }

    override fun startDisplayHomeworkActivity() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val sharedView = this@MainActivity.findViewById<View>(R.id.toolbar_main)
            val transitionName = getString(R.string.transition_tooblar)
            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, sharedView, transitionName)
            startActivity(Intent(this@MainActivity, DisplayAssignmentsActivity::class.java),
                    transitionActivityOptions.toBundle())
        } else {
            startActivity(Intent(this@MainActivity, DisplayAssignmentsActivity::class.java))
        }
    }

    override fun startMainActivity() {
        val toolbarTitle = findViewById<View>(R.id.toolbar_title) as TextView
        AnimationUtils.textFade(toolbarTitle, getString(R.string.chat),
                resources.getInteger(android.R.integer.config_shortAnimTime))
        val toolbarIcon = findViewById<View>(R.id.toolbar_menu_icon) as ImageView
        AnimationUtils.fadeIn(toolbarIcon, resources.getInteger(android.R.integer.config_shortAnimTime))

        val fragment = ChatFragment()
        supportFragmentManager.changeFragmentRightAnimated(
                R.id.fragment_container_main, fragment, false, true)
    }

    override fun notifyNoInternetConnection() {
        AlertDialogFragment.newInstance(getString(R.string.no_internet_connection),
                getString(R.string.cannot_send_messages_internet_connection), positiveString = "Ok",haveNegative = false)
                .show(supportFragmentManager, AlertDialogFragment::class.java.name)
    }

    override fun onAlertPositiveClicked(dialog: Dialog) {
        dialog.dismiss()
    }

    override fun onAlertNegativeClicked(dialog: Dialog) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
