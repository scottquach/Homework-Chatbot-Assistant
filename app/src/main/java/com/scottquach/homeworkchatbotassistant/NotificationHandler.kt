package com.scottquach.homeworkchatbotassistant

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import com.scottquach.homeworkchatbotassistant.activities.SignInActivity
import com.scottquach.homeworkchatbotassistant.receivers.NotificationReplyReceiver
import timber.log.Timber

/**
 * Created by Scott Quach on 11/18/2017.
 *
 * Responsible for handling notifications sent out by the app
 */
class NotificationHandler {



    fun NotifyClassEnd(context: Context, userClass: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_1", "Class Ended", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.description = context.getString(R.string.notify_title)
            channel.enableVibration(true)

            notificationManager.createNotificationChannel(channel)
        }

        val remoteInput = RemoteInput.Builder(Constants.RESULT_KEY)
                .setLabel(context.getString(R.string.notify_reply_label))
                .build()
        val replyIntent = Intent(context, NotificationReplyReceiver::class.java)
        val replyPendingIntent = PendingIntent.getBroadcast(context, 203,
                replyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val action = NotificationCompat.Action.Builder(R.drawable.ic_send, context.getString(R.string.notify_reply_label), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()


        Timber.d("showing notification")
        val intent = Intent(context, SignInActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 102, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, "channel_1")
                .setContentTitle(context.getString(R.string.notify_title))
                .setContentText("Do you have any homework for $userClass?")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(action)

        val notification = builder.build()
        notificationManager.notify(1011, notification)
    }

    fun updateNotificationConversation(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_1", "Class Ended", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.description = "Homework Assistant"
            channel.enableVibration(true)

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, SignInActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 102, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("Homework Assistant")
        bigTextStyle.bigText(message)

        val builder = NotificationCompat.Builder(context, "channel_1")
                .setContentTitle("Homework Assistant")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setRemoteInputHistory(arrayOf(message.subSequence(0, message.lastIndex)))
                .setStyle(bigTextStyle)

        val notification = builder.build()
        notificationManager.notify(1011, notification)
    }

    fun NotifyAssignmentDue(context: Context, userAssignment: String, userClass: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_2", "Assignments Due", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.description = context.getString(R.string.notify_title)
            channel.enableVibration(true)

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, SignInActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 103, intent, PendingIntent.FLAG_UPDATE_CURRENT)

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