package com.scottquach.homeworkchatbotassistant.jobs

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
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.activities.SignInActivity

/**
 * Created by Scott Quach on 10/16/2017.
 */
class JobNotifyAssignmentDue : JobService() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStopJob(jobParameters: JobParameters?): Boolean {
        val userAssignment = jobParameters?.extras!!.getString(Constants.USER_ASSIGNMENT)
        createNotification(this, userAssignment)

        val handler = MessageHandler(this)
        handler.assignmentDueReminder(userAssignment)
        return false
    }

    override fun onStartJob(jobParameters: JobParameters?): Boolean {
        return true
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