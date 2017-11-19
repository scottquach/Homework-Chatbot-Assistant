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
import com.scottquach.homeworkchatbotassistant.NotificationHandler
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
            NotificationHandler().updateNotificationConversation(context, "Error")
        }

    }

    override fun messagesCallback(model: MessageModel) {
        NotificationHandler().updateNotificationConversation(context, model.message)
    }

    private fun getMessageText(intent: Intent) : CharSequence? {
        return RemoteInput.getResultsFromIntent(intent)?.getCharSequence(Constants.RESULT_KEY)
    }
}