package com.scottquach.homeworkchatbotassistant.contracts

import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel

/**
 * Created by Scott Quach on 10/27/2017.
 */
interface CreateClassContract {
    interface View {
        fun notifyNoInternet()
        fun notifyClassCreated()
        fun notifyMissingRequiredFields()
        fun showEndTimePicker()
        fun showDayPicker()
        fun updateDayOfWeekView(message: String)
        fun updateEndTimeView(message: String)
    }

    interface Presenter {
        fun onCreateClassAttempt()
        fun onCancelCreateClass()
        fun addNewClass(model: ClassModel)
        fun convertToDayFormat()
        fun isRequiredFieldsFilled(): Boolean
        fun onPickEndTime()
        fun onPickDay()
        fun onToggleDayPicked(isChecked: Boolean, index: Int)
        fun setEndTime(time: TimeModel)
        fun setDaysOfWeek()
    }
}