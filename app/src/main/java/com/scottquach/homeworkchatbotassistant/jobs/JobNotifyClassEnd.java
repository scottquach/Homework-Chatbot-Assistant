package com.scottquach.homeworkchatbotassistant.jobs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.scottquach.homeworkchatbotassistant.MessageHandler;
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager;
import com.scottquach.homeworkchatbotassistant.R;
import com.scottquach.homeworkchatbotassistant.activities.SignInActivity;

import timber.log.Timber;

/**
 * Created by scott on 10/11/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobNotifyClassEnd extends JobService{

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        notifyUser(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private void notifyUser(JobParameters jobParameters) {
        Timber.d("ON RECEIVE WAS CALLED");
//            Timber.d("Class was " + className);
            MessageHandler messageHandler = new MessageHandler(this);
            messageHandler.promptForAssignment("temporary");

            createNotification(this, "temporary");
            NotifyClassEndManager manager = new NotifyClassEndManager(this);
            manager.startManaging();
            jobFinished(jobParameters, false);
    }

    private void createNotification(Context context, String className) {
        Intent intent = new Intent(context, SignInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 102, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "app_channel")
                .setContentTitle("Homework Tracker")
                .setContentText("do you have any homework for " + className)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1011, notification);
    }
}
