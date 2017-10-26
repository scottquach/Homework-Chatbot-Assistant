package com.scottquach.homeworkchatbotassistant.jobs

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
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
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class JobNotifyClassEnd : JobService() {

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Timber.d("onStartJob called")
        notifyUser(jobParameters, jobParameters.extras.getString(Constants.CLASS_NAME))
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }

    private fun notifyUser(jobParameters: JobParameters, userClass: String?) {
        Timber.d("ON RECEIVE WAS CALLED")
        //            Timber.d("Class was " + className);
        val manager = NotifyClassEndManager(this)
        manager.startManaging(jobParameters.extras.getLong(Constants.CLASS_END_TIME))

        val messageHandler = MessageHandler(this)
        messageHandler.promptForAssignment(userClass!!)

        createNotification(this, userClass)

        jobFinished(jobParameters, false)
    }

    private fun createNotification(context: Context, userClass: String) {
        val intent = Intent(context, SignInActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 102, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, "app_channel")
                .setContentTitle("Homework Tracker")
                .setContentText("do you have any homework for " + userClass)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val notification = builder.build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1011, notification)
    }
}
