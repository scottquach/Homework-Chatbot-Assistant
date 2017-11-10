package com.scottquach.homeworkchatbotassistant.presenters

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.scottquach.homeworkchatbotassistant.utils.InstrumentationUtils
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager
import com.scottquach.homeworkchatbotassistant.contracts.CreateClassContract
import com.scottquach.homeworkchatbotassistant.fragments.CreateClassFragment
import com.scottquach.homeworkchatbotassistant.logEvent
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import com.scottquach.homeworkchatbotassistant.utils.NetworkUtils
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_create_class.*

/**
 * Created by Scott Quach on 10/27/2017.
 */
class CreateClassPresenter(val view: CreateClassFragment) : CreateClassContract.Presenter {

    private var selectedDays = mutableListOf<Int>()
    private var timeEnd: TimeModel? = null

    /**
     * Sets the class end time and calls the view to update the view
     */
    override fun setEndTime(time: TimeModel) {
        timeEnd = time
        view.updateEndTimeView(StringUtils.getTimeString(time))
    }

    /**
     * Calls for the selectedDays to be converted and updates the view
     */
    override fun setDaysOfWeek() {
        convertToDayFormat()
        view.updateDayOfWeekView(StringUtils.getDaysOfWeek(selectedDays))
    }

    /**
     * Called every time a check box is changed in day selector picker. Determines if
     * the day is removed or added from selectedDays
     */
    override fun onToggleDayPicked(isChecked: Boolean, index: Int) {
        if (isChecked) {
            selectedDays.add(index)
        } else if (selectedDays.contains(index)) {
            selectedDays.remove(index)
        }
    }

    /**
     * Calls to display the time picker dialog
     */
    override fun onPickEndTime() {
        view.showEndTimePicker()
    }

    /**
     * Calls to display the day picker dialog
     */
    override fun onPickDay() {
        view.showDayPicker()
    }

    /**
     * converts the selectedDay array into a format that starts
     * with 1 being Sunday instead of 0
     */
    override fun convertToDayFormat() {
        for (i in selectedDays.indices) {
            selectedDays[i] = selectedDays[i] + 1
        }
    }

    /**
     * Checks whether all input fields are filled before attempting to
     * create a new class
     */
    override fun isRequiredFieldsFilled(): Boolean {
        return (!view.edit_title.text.isEmpty() && timeEnd != null && selectedDays.isNotEmpty())
    }


    /**
     * Determines if the create class attempt is valid, if it is create the new class model and
     * call for it to be added in database
     */
    override fun onCreateClassAttempt() {
        if (NetworkUtils.isConnected(view.context)) {
            if (isRequiredFieldsFilled()) {
                val newClass = ClassModel()
                newClass.title = view.edit_title.text.toString().trim()
                newClass.days = selectedDays
                newClass.timeEnd = timeEnd!!
                addNewClass(newClass)
                logEvent(InstrumentationUtils.ADD_CLASS)
            } else view.notifyMissingRequiredFields()
        } else view.notifyNoInternet()
    }

    override fun onCancelCreateClass() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Adds the new created class to the database and calls for the view to exit
     */
    override fun addNewClass(model: ClassModel) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        databaseReference.child("users").child(user!!.uid).child("classes").child(model.title).setValue(model)
        val manager = NotifyClassEndManager(view.context)
        manager.startManaging()

        view.returnToScheduleView()
    }


}