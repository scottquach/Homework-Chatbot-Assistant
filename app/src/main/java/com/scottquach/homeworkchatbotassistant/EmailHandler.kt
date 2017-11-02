package com.scottquach.homeworkchatbotassistant

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Created by Scott Quach on 10/31/2017.
 * Handles the ability to email the developer back in order to report feedback or to report
 * bugs/errors
 */
class EmailHandler(val context: Context) {

    fun sendFeedbackEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", context.getString(R.string.email_email_to), null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_email_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_feedback_starting_text))

        try {
            context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.email_select_client)))
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.email_error_sending), Toast.LENGTH_SHORT).show()
        }
    }
}