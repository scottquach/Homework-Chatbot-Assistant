package com.scottquach.homeworkchatbotassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Scott Quach on 9/16/2017.
 */

public class PromptHomeworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PromptHomeworkManager manager = new PromptHomeworkManager(context);
        if (intent.getExtras() != null) {
            String className = intent.getExtras().getString("class_name", "class");
            MessageHandler messageHandler = new MessageHandler();
            messageHandler.promptForHomework(className);
        }

        manager.determineNextAlarm();
    }
}
