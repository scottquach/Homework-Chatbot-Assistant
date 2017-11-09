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
import com.scottquach.homeworkchatbotassistant.Constants
import com.scottquach.homeworkchatbotassistant.MessageHandler
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.activities.SignInActivity

/**
 * Created by Scott Quach on 10/16/2017.
 *
 * Job that notifies the user through notification and in app message about upcoming assignment
 * due dates
 */
class JobNotifyAssignmentDue : JobService() {
    override fun onStopJob(jobParameters: JobParameters?): Boolean {
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        val userAssignment = jobParameters.extras.getString(Constants.USER_ASSIGNMENT)
        val userClass = jobParameters.extras.getString(Constants.USER_CLASS)

        createNotification(this, userAssignment, userClass)

        val handler = MessageHandler(this, this)
        handler.assignmentDueReminder(userAssignment, userClass)
        jobFinished(jobParameters, false)
        return true
    }

     fun createNotification(context: Context, userAssignment: String, userClass: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_2", "assignment_channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.description = context.getString(R.string.notify_title)
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