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
        val emailIntent = Intent(Intent.ACTION_SEND)

        emailIntent.data = Uri.parse("chuckglobal@gmail.com")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("chuckglobal@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Homework Assistant Feedback")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Type Feedback Below")

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } catch (e: Exception) {
            Toast.makeText(context, "Error sending feedback email", Toast.LENGTH_SHORT).show()
        }
    }
}