package com.scottquach.homeworkchatbotassistant

import com.scottquach.homeworkchatbotassistant.models.TimeModel

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
        fun getDaysOfWeek(selectedDays: MutableList<Int>) : String {
            var displayString = ""
            for (day in selectedDays) {
                when(day) {
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
                return (model.timeEndHour - 12).toString() + ":" + model.timeEndMinute + " PM"
            } else {
                return (model.timeEndHour).toString() + ":" + model.timeEndMinute + " AM"
            }
        }
    }
}