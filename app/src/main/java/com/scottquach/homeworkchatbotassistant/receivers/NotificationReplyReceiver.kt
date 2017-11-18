package com.scottquach.homeworkchatbotassistant.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.RemoteInput
import timber.log.Timber
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.scottquach.homeworkchatbotassistant.Constants
import com.scottquach.homeworkchatbotassistant.database.MessageDatabaseHandler
import com.scottquach.homeworkchatbotassistant.MessageType
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.activities.SignInActivity
import com.scottquach.homeworkchatbotassistant.models.MessageModel


/**
 * Created by Scott Quach on 11/6/2017.
 * Handles notification replies from notifying the user when the class ends
 */
class NotificationReplyReceiver : BroadcastReceiver(), MessageDatabaseHandler.CallbackInterface {

    private lateinit var messageDatabaseHandler: MessageDatabaseHandler
    private lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        messageDatabaseHandler = MessageDatabaseHandler(context, this@NotificationReplyReceiver)

        val message = getMessageText(intent).toString()
        Timber.d("Reply was " + message)

        if (message != null) {
            messageDatabaseHandler.addMessage(MessageType.SENT, message)
            messageDatabaseHandler.processNewMessage(message)
        } else {
            updateNotification(context, "Error")
        }

    }

    override fun messagesCallback(model: MessageModel) {
        updateNotification(context, model.message)
    }

    private fun updateNotification(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_1", "class_channel", NotificationManager.IMPORTANCE_HIGH)
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
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setStyle(bigTextStyle)

        val notification = builder.build()
        notificationManager.notify(1011, notification)
    }

    private fun getMessageText(intent: Intent) : CharSequence? {
        return RemoteInput.getResultsFromIntent(intent)?.getCharSequence(Constants.RESULT_KEY)
    }
}