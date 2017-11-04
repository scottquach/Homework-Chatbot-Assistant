package com.scottquach.homeworkchatbotassistant.jobs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat

import com.scottquach.homeworkchatbotassistant.Constants
import com.scottquach.homeworkchatbotassistant.MessageHandler
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager
import com.scottquach.homeworkchatbotassistant.R
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

        val messageHandler = MessageHandler(this)
        messageHandler.promptForAssignment(userClass!!)

        val manager = NotifyClassEndManager(this)
        manager.startManaging(jobParameters.extras.getLong(Constants.CLASS_END_TIME))

        jobFinished(jobParameters, false)
    }

    private fun createNotification(context: Context, userClass: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_1", "class_channel", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.description = "Homework Assistant"
            channel.enableVibration(true)

            notificationManager.createNotificationChannel(channel)
        }
        Timber.d("showing notification")
        val intent = Intent(context, SignInActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 102, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, "channel_1")
                .setContentTitle("Homework Assistant")
                .setContentText("Give me homework for " + userClass)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)

        val notification = builder.build()
        notificationManager.notify(1011, notification)
    }
}
