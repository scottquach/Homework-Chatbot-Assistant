package com.scottquach.homeworkchatbotassistant

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.support.v4.content.ContextCompat
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import java.util.*

/**
 * Created by Scott Quach on 11/15/2017.
 * Class that manages the apps interactions with the systems calendar. Mainly used for adding/removing
 * assignments from the calendar
 */
class CalendarHandler {
    fun addAssignmentToCalendar(context: Context, model: AssignmentModel) {
        if (BaseApplication.getInstance().sharePref.getBoolean(Constants.ADD_ASSIGNMENTS_TO_CALENDAR, true)) {
            val dueDate = StringUtils.convertStringToCalendar(context, model.dueDate)
            dueDate.set(Calendar.HOUR_OF_DAY, 9)
            dueDate.set(Calendar.MINUTE, 0)

            val contentResolver = context.contentResolver
            val contentValues = ContentValues()

            contentValues.put(CalendarContract.Events.TITLE, model.title)
            contentValues.put(CalendarContract.Events.DESCRIPTION, "Assignment is due for ${model.userClass}")
            contentValues.put(CalendarContract.Events.DTSTART, dueDate.timeInMillis)

            contentValues.put(CalendarContract.Events.CALENDAR_ID, 1)
            contentValues.put(CalendarContract.Events.DURATION, "+P1H")
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)
            }
        }
    }
}