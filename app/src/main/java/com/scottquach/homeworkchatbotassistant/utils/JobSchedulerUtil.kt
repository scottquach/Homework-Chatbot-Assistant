package com.scottquach.homeworkchatbotassistant.utils

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import android.support.annotation.RequiresApi

import com.scottquach.homeworkchatbotassistant.Constants
import com.scottquach.homeworkchatbotassistant.jobs.JobNotifyAssignmentDue
import com.scottquach.homeworkchatbotassistant.jobs.JobNotifyClassEnd

import timber.log.Timber

/**
 * Created by scott on 10/11/2017.
 * Contains all helper methods that are used to manage jobs from
 * JobScheduler
 */

object JobSchedulerUtil {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun scheduleClassManagerJob(context: Context, userClass: String,
                                minimumLatency: Long, overrideDelay: Long, specificTime: Long) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val bundle = PersistableBundle()
        bundle.putString(Constants.CLASS_NAME, userClass)
        bundle.putLong(Constants.CLASS_END_TIME, specificTime)

        jobScheduler.schedule(JobInfo.Builder(Constants.JOB_CLASS_MANAGER,
                ComponentName(context, JobNotifyClassEnd::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setMinimumLatency(minimumLatency)
//                .setOverrideDeadline(overrideDelay)
                .setExtras(bundle)
                .build())
        Timber.d("Class end scheduled")
        Timber.d("minlatency was $minimumLatency override delay was $overrideDelay")
    }

    /**
     * Schedules a job that will notify the user when an assignment is due. User is notified 1 day
     * before the assignment is due around 5pm
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun scheduleAssignmentManagerJob(context: Context, userAssignment: String, userClass: String,
                                     minimumDelay: Long, overrideDelay: Long) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val bundle = PersistableBundle()
        bundle.putString(Constants.USER_ASSIGNMENT, userAssignment)
        bundle.putString(Constants.USER_CLASS, userClass)

        Timber.d("Assignment is $userAssignment")

        jobScheduler.schedule(JobInfo.Builder(System.currentTimeMillis().toInt(),
                ComponentName(context, JobNotifyAssignmentDue::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setMinimumLatency(minimumDelay)
//                .setOverrideDeadline(overrideDelay)
                .setExtras(bundle)
                .build())

        Timber.d("Assignment due scheduled")
    }

    /**
     * Cancels all jobs that originated from this app package
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun cancelAllJobs(context: Context) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancelAll()
    }


    /**
     * Cancels a specific job by job id
     * @param context
     * @param jobId
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun cancelJob(context: Context, jobId: Int) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(jobId)
    }
}
