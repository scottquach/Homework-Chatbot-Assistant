package com.scottquach.homeworkchatbotassistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.scottquach.homeworkchatbotassistant.utils.JobSchedulerUtil

/**
 * Created by Scott Quach on 11/4/2017.
 */
class TimeChangeReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, p1: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobSchedulerUtil.cancelAllJobs(context)
        }
        val manager = NotifyClassEndManager(context)
        manager.startManaging(System.currentTimeMillis())
    }
}