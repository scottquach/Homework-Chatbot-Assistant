package com.scottquach.homeworkchatbotassistant;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

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
        }
        PromptHomeworkManager manager = new PromptHomeworkManager(context);
    }

    private void createNotification(Context context, String className) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "app_channel")
                .setContentTitle("Homework Tracker")
                .setContentText("do you have any homework for " + className)
                .setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1011, notification);
        Timber.d("creating notification");
    }
}
