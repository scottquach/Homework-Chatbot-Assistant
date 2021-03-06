package com.scottquach.homeworkchatbotassistant.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.scottquach.homeworkchatbotassistant.Constants;
import com.scottquach.homeworkchatbotassistant.database.MessageDatabaseHandler;
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager;
import com.scottquach.homeworkchatbotassistant.R;
import com.scottquach.homeworkchatbotassistant.activities.SignInActivity;

import timber.log.Timber;

/**
 * Created by Scott Quach on 9/16/2017.
 */

public class NotifyClassEndReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("ON RECEIVE old WAS CALLED");
        if (intent.getExtras() != null) {
            String className = intent.getExtras().getString(Constants.CLASS_NAME, "class");
            MessageDatabaseHandler messageDatabaseHandler = new MessageDatabaseHandler(context, null);
            messageDatabaseHandler.promptForAssignment(className);

            createNotification(context, className);
            NotifyClassEndManager manager = new NotifyClassEndManager(context);
            manager.startManaging(System.currentTimeMillis());
        }
    }

    private void createNotification(Context context, String className) {
        Intent intent = new Intent(context, SignInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 102, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "app_channel")
                .setContentTitle(context.getString(R.string.notify_title))
                .setContentText("do you have any homework for " + className)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1011, notification);
    }
}
