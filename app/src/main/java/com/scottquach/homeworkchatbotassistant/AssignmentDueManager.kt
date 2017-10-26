package com.scottquach.homeworkchatbotassistant

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.utils.JobSchedulerUtil
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Scott Quach on 9/23/2017.
 */

class AssignmentDueManager(var context: Context) {

    /**
     * Gets the due date for assignment and configures minimumDelay and overrideDelay for when
     * scheduling the job to notify the user. User will be notify the day before assignment is due
     * roughly around 5pm. Calls a job if above api 21, else uses alarm manager
     */
    fun startNextAlarm(model: AssignmentModel) {

        val alarm = StringUtils.convertStringToCalendar(context, model.dueDate)
        alarm.add(Calendar.DAY_OF_MONTH, -1)
        alarm.set(Calendar.HOUR_OF_DAY, 17)
        alarm.set(Calendar.MINUTE, 0)

        Timber.d("Assignment Notify is " + alarm.timeInMillis)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarm.add(Calendar.MINUTE, -30)
            val minimumDelay = alarm.timeInMillis - System.currentTimeMillis()
            alarm.add(Calendar.MINUTE, 60)
            val overrideDelay = alarm.timeInMillis - System.currentTimeMillis()


            JobSchedulerUtil.scheduleAssignmentManagerJob(context, model.title, model.userClass,
                    minimumDelay, overrideDelay)
        } else {
            val intent = Intent(context, AssignmentDueReceiver::class.java)
            intent.setClass(context, AssignmentDueReceiver::class.java)
            intent.putExtra(Constants.USER_ASSIGNMENT, model.title)
            val pendingIntent = PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(),
                    intent, PendingIntent.FLAG_CANCEL_CURRENT)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pendingIntent)

            Timber.d("Homework alarm set for " + alarm)
        }


    }
}
