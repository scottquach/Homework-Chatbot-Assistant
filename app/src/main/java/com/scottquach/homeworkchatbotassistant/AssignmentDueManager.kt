package com.scottquach.homeworkchatbotassistant

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.scottquach.homeworkchatbotassistant.database.AssignmentDatabase
import com.scottquach.homeworkchatbotassistant.receivers.AssignmentDueReceiver
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.utils.JobSchedulerUtil
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import timber.log.Timber
import java.util.*

/**
 * Created by Scott Quach on 9/23/2017.
 */

class AssignmentDueManager(var context: Context) : AssignmentDatabase.AssignmentCallback {


    private val database by lazy {
        AssignmentDatabase(this)
    }

    /**
     * Gets the due date for assignment and configures minimumDelay and overrideDelay for when
     * scheduling the job to notify the user. User will be notify the day before assignment is due
     * roughly around 5pm. Calls a job if above api 21, else uses alarm manager. Reminder will only
     * be sent if the reminder time is after current time
     */
    fun scheduleNextAlarm(model: AssignmentModel) {

        val alarm = StringUtils.convertStringToCalendar(context, model.dueDate)
        alarm.add(Calendar.DAY_OF_MONTH, -1)
        alarm.set(Calendar.HOUR_OF_DAY, 17)
        alarm.set(Calendar.MINUTE, 0)

        Timber.d("Assignment Notify is " + alarm.timeInMillis)

        val currentTime = Calendar.getInstance()
        if (alarm.after(currentTime)) {
            Timber.d("Assignment reminder is after current time")
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

    /**
     * Usually called when an assignment is deleted and the jobs schedules for assignments is cleared.
     * Requests an updated list of user assignments from database. Logic for reschedule occurs
     * in assignmentsCallback
     */
    fun requestReschedule() {
        database.loadAssignments()
    }

    /**
     * Called once data is loaded and will go through the process of rescheduling the jobs for all
     * user assignments
     */
    override fun assignmentsCallback(data: List<AssignmentModel>) {
        for (model in data) {
            scheduleNextAlarm(model)
        }
    }
}
