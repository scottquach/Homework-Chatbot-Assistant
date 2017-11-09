package com.scottquach.homeworkchatbotassistant.jobs

import android.app.*
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.receivers.NotificationReplyReceiver

import com.scottquach.homeworkchatbotassistant.activities.SignInActivity

import timber.log.Timber

/**
 * Created by scott on 10/11/2017.
 * When called, notifies the user through a notification and in app message to provide homework
 * for the ending class. Calls the NotifyClassEndManager to schedule the job for the next class
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class JobNotifyClassEnd : JobService() {

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Timber.d("onStartJob called")
        notifyUser(jobParameters, jobParameters.extras.getString(Constants.CLASS_NAME))
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return true
    }

    private fun notifyUser(jobParameters: JobParameters, userClass: String?) {
        Timber.d("ON RECEIVE WAS CALLED")
        createNotification(this, userClass!!)

        val messageHandler = MessageHandler(this, this)
        messageHandler.promptForAssignment(userClass!!)

        val manager = NotifyClassEndManager(this)
        manager.startManaging(jobParameters.extras.getLong(Constants.CLASS_END_TIME))

        jobFinished(jobParameters, false)
    }

    fun createNotification(context: Context, userClass: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_1", "class_channel", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.description = context.getString(R.string.notify_channel_class)
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
                .setContentText(context.getString(R.string.notify_assignment_due_text) + userClass)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(action)

        val notification = builder.build()
        notificationManager.notify(1011, notification)
    }
}
