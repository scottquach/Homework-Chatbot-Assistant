package com.scottquach.homeworkchatbotassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Scott Quach on 10/4/2017.
 * Re initialize class end reminder alarms that were destroyed on device reboot
 */

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotifyClassEndManager manager = new NotifyClassEndManager(context);
        manager.startManaging(System.currentTimeMillis());
    }
}
