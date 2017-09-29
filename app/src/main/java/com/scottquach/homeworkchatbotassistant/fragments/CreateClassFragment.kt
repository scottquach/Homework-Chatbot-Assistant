package com.scottquach.homeworkchatbotassistant.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import kotlinx.android.synthetic.main.fragment_create_class.*
import timber.log.Timber

class CreateClassFragment : Fragment() {

    private var listener: CreateClassInterface? = null

    private var timeEnd: TimeModel? = null
    private var selectedDays: MutableList<Int> = emptyList<Int>().toMutableList()

    interface CreateClassInterface {
        fun addClass(newClass: ClassModel)
        fun switchToDisplayFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_create_class)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        floating_confirm.setOnClickListener {
            if (isRequiredFieldsFilled()) {
                var newClass = createNewClassModel()
                listener?.let {
                    it.addClass(newClass)
                    it.switchToDisplayFragment()
                }
            } else Toast.makeText(context, "Missing required fields", Toast.LENGTH_SHORT).show()
        }

        floating_cancel.setOnClickListener {
            listener?.switchToDisplayFragment()
        }

        button_change_end_time.setOnClickListener {
            var fragment = TimePickerFragment.newInstance(Constants.TIME_PICKER_END)
            fragment.setTargetFragment(this, Constants.TIME_PICKER_END)
            fragment.show(fragmentManager, "timePicker")
        }

        button_day_picker.setOnClickListener {
            showDayPickerDialog()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CreateClassInterface) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun createNewClassModel(): ClassModel {
        var newClassModel = ClassModel()
        newClassModel.title = edit_title.text.toString()
        newClassModel.timeEnd = this.timeEnd!!
        newClassModel.days = selectedDays
        return newClassModel
    }

    fun setEndTime(tag: Int, time: TimeModel) {
        Timber.d("set time was called " + tag)
        text_end_time.text = StringUtils.getTimeString(time)
        timeEnd = time
    }

    private fun showDayPickerDialog() {
        selectedDays.clear()
        val items: Array<String> = resources.getStringArray(R.array.days_of_week)

        val dialog = AlertDialog.Builder(context)
                .setTitle("temporary title")
                .setMultiChoiceItems(items, null, { dialogInterface: DialogInterface?, index: Int, isChecked: Boolean ->
                    if (isChecked) {
                        selectedDays.add(index)
                    } else if (selectedDays.contains(index)) {
                        selectedDays.remove(index)
                    }
                    Timber.d(selectedDays.toString())
                })
                .setPositiveButton(getString(R.string.set), { dialogInterface, i ->
                    convertToDayFormat()
                    text_day_display.text = StringUtils.getDaysOfWeek(selectedDays)
                })
                .setNegativeButton(getString(R.string.cancel), { dialogInterface, i ->  })
                .create().show()
    }

    /**
     * converts the selectedDay array into a format that starts
     * with 1 being Sunday instead of 0
     */
    private fun convertToDayFormat() {
        for (i in selectedDays.indices) {
            selectedDays[i] = selectedDays[i] + 1
        }
        Timber.d("converted array " + selectedDays)
    }

    private fun isRequiredFieldsFilled(): Boolean {
        return (!edit_title.text.isEmpty() && timeEnd != null && selectedDays.isNotEmpty())
    }
}
