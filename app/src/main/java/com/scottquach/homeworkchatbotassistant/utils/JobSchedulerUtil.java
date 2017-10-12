package com.scottquach.homeworkchatbotassistant.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;

import com.scottquach.homeworkchatbotassistant.Constants;
import com.scottquach.homeworkchatbotassistant.jobs.JobNotifyClassEnd;

import timber.log.Timber;

/**
 * Created by scott on 10/11/2017.
 */

public class JobSchedulerUtil {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleClassManagerJob(Context context, String userClass,
                                               long minimumLatency, long overrideDelay) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("class_name", userClass);

        if (jobScheduler != null) {
            jobScheduler.schedule(new JobInfo.Builder((int) System.currentTimeMillis(),
                    new ComponentName(context, JobNotifyClassEnd.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setMinimumLatency(minimumLatency)
                    .setOverrideDeadline(overrideDelay)
                    .setExtras(bundle)
                    .build());
            Timber.d("Job scheduled");
        }
        Timber.d("minlatency was " + minimumLatency + " override delay was "  +overrideDelay);
    }

    public static void scheduleAssignmentManagerJob(Context context) {

    }
}
