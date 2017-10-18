package com.scottquach.homeworkchatbotassistant

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.scottquach.homeworkchatbotassistant.activities.SignInActivity

/**
 * Created by Scott Quach on 9/23/2017.
 */

class AssignmentDueReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras != null) {
            val userAssignment = intent.extras.getString(Constants.USER_ASSIGNMENT)
            createNotification(context, userAssignment)

            val handler = MessageHandler(context)
            handler.assignmentDueReminder(userAssignment)
        }
    }

    private fun createNotification(context: Context, userAssignment: String) {
        val intent = Intent(context, SignInActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 103, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, "app_channel")
                .setContentTitle("Homework Tracker")
                .setContentText("\"$userAssignment\" is due tomorrow")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val notification = builder.build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1012, notification)


    }
}
