package com.scottquach.homeworkchatbotassistant.utils

import android.content.Context
import android.widget.Toast
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Scott Quach on 9/28/2017.
 */
class StringUtils {

    companion object {

        /**
         * Takes in a list full of ints the specify a day of the week from 1 to 7. Then
         * returns a string corresponding to those days. So {1,2,3} would return SMT for Sunday,
         * Monday, Tuesday
         */
        fun getDaysOfWeek(selectedDays: MutableList<Int>): String {
            var displayString = ""
            for (day in selectedDays) {
                when (day) {
                    1 -> displayString += "S"
                    2 -> displayString += "M"
                    3 -> displayString += "T"
                    4 -> displayString += "W"
                    5 -> displayString += "T"
                    6 -> displayString += "F"
                    7 -> displayString += "S"
                }
            }
            return displayString
        }

        fun getTimeString(model: TimeModel): String {
            if (model.timeEndHour > 12) {
                return if (model.timeEndMinute > 9) {
                    (model.timeEndHour - 12).toString() + ":" + model.timeEndMinute + " PM"
                } else {
                    (model.timeEndHour - 12).toString() + ":0" + model.timeEndMinute + " PM"
                }

            } else {
                return if (model.timeEndMinute > 9) {
                    (model.timeEndHour).toString() + ":" + model.timeEndMinute + " AM"

                } else {
                    (model.timeEndHour).toString() + ":0" + model.timeEndMinute + " AM"
                }
            }
        }

        fun convertStringToCalendar(context: Context, stringDate: String): Calendar {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            var convertedDate: Date? = null
            try {
                convertedDate = dateFormat.parse(stringDate)
            } catch (e: ParseException) {
                Timber.e(e, "couldn't convert string date")
                Toast.makeText(context, "Couldn't set reminder", Toast.LENGTH_SHORT).show()
            }
            Timber.d("converted date is " + convertedDate)

            val calendar = Calendar.getInstance()
            calendar.time = convertedDate
            return calendar
        }

        /**
         * Returns a string that is converted from the stored YYY/MM/DD to the more known
         * MM/DD/YYYY
         */
        fun convertStoredDateToAmericanDate(stringDate: String) : String {
            val oldDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val newDateFormat = SimpleDateFormat("MM-dd-yyyy")

            var date: Date? = null
            try {
                date = oldDateFormat.parse(stringDate)
            } catch (e: ParseException) {
                Timber.e(e, "couldn't convert oldDateFormat to newDateFormat")
            }

            return if (date != null) {
                newDateFormat.format(date)
            } else stringDate
        }
    }
}