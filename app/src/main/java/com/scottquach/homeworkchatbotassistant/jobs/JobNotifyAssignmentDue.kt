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
import com.scottquach.homeworkchatbotassistant.NotificationHandler
import com.scottquach.homeworkchatbotassistant.database.MessageDatabaseHandler
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

        NotificationHandler().NotifyAssignmentDue(this, userAssignment, userClass)

        val handler = MessageDatabaseHandler(this, this)
        handler.assignmentDueReminder(userAssignment, userClass)
        jobFinished(jobParameters, false)
        return true
    }
}