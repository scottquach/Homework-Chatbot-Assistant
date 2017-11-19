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
import com.scottquach.homeworkchatbotassistant.database.MessageDatabaseHandler

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

    private fun notifyUser(jobParameters: JobParameters, userClass: String) {
        Timber.d("ON RECEIVE WAS CALLED")
        NotificationHandler().NotifyClassEnd(this, userClass)

        val messageHandler = MessageDatabaseHandler(this, this)
        messageHandler.promptForAssignment(userClass!!)

        val manager = NotifyClassEndManager(this)
        manager.startManaging(jobParameters.extras.getLong(Constants.CLASS_END_TIME))

        jobFinished(jobParameters, false)
    }
}
