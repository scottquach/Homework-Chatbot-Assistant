package com.scottquach.homeworkchatbotassistant.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.scottquach.homeworkchatbotassistant.Constants
import com.scottquach.homeworkchatbotassistant.MessageHandler
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.activities.SignInActivity

/**
 * Created by Scott Quach on 9/23/2017.
 */

class AssignmentDueReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras != null) {
            val userAssignment = intent.extras.getString(Constants.USER_ASSIGNMENT)
            val userClass = intent.extras.getString(Constants.USER_CLASS)
            createNotification(context, userAssignment, userClass)

            val handler = MessageHandler(context, this)
            handler.assignmentDueReminder(userAssignment, userClass)
        }
    }

    private fun createNotification(context: Context, userAssignment: String, userClass:String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_2", "assignment_channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.description = "Homework Assistant"
            channel.enableVibration(true)

        }

        val intent = Intent(context, SignInActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 103, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, "channel_2")
                .setContentTitle(context.getString(R.string.notify_title))
                .setContentText("\"$userAssignment\" is due tomorrow for $userClass")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)

        val notification = builder.build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
