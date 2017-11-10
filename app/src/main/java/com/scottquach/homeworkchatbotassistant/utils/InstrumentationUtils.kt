package com.scottquach.homeworkchatbotassistant.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by Scott Quach on 11/8/2017.
 * Sends instrumentation data to Firebase Analytics
 */
class InstrumentationUtils(val context: Context) {

    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(event: String) {
        val bundle = Bundle()

        firebaseAnalytics.logEvent(event, bundle)
    }

    companion object {
        val SIGN_OUT = "sign_out"
        val LOGIN_FAIL = "login_fail"
        val ADD_CLASS = "add_class"
        val DELETE_CLASS = "delete_class"
        val DELETE_ASSIGNMENT = "delete_assignment"
        val ADDED_ASSIGNMENT = "added_assignment"
        val SEND_FEEDBACK = "send_feedback"
        val USER_SENT_MESSAGE = "user_sent_message"
        val REQUEST_HELP = "request_help"
        val REQUEST_EXAMPLES = "request_examples"
        val REQUEST_NEXT_ASSIGNMENT = "request_next_assignment"
        val REQUEST_OVERDUE_ASSIGNMENTS = "request_overdue_assignments"
        val REQUEST_CURRENT_ASSIGNMENTS = "request_current_assignments"
    }

}