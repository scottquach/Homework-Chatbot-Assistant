package com.scottquach.homeworkchatbotassistant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.scottquach.homeworkchatbotassistant.activities.SignInActivity;

import timber.log.Timber;

/**
 * Created by Scott Quach on 9/16/2017.
 */

public class PromptHomeworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("ON RECEIVE WAS CALLED");
        if (intent.getExtras() != null) {
            String className = intent.getExtras().getString("class_name", "class");
            MessageHandler messageHandler = new MessageHandler();
            messageHandler.promptForHomework(className);

            createNotification(context, className);
            PromptHomeworkManager manager = new PromptHomeworkManager(context);
            manager.startManaging();
        }

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
        Timber.d("creating notification");
    }
}
