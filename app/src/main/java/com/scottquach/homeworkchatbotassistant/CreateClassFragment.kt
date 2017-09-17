package com.scottquach.homeworkchatbotassistant

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import kotlinx.android.synthetic.main.fragment_create_class.*
import timber.log.Timber
import java.sql.Timestamp

class CreateClassFragment : Fragment() {

    private var listener: CreateClassInterface? = null
    private var timeStart: Long = 0
    private var timeEnd: Long = 0

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
            var newClass = createNewClassModel()
            listener?.let {
                it.addClass(newClass)
                it.switchToDisplayFragment()
            }

        }

        floating_cancel.setOnClickListener {
            listener?.switchToDisplayFragment()
        }

        button_change_start_time.setOnClickListener {
            var fragment = TimePickerFragment.newInstance(Constants.TIME_PICKER_START)
            fragment.setTargetFragment(this, Constants.TIME_PICKER_START)
            fragment.show(fragmentManager, "timePicker")
        }

        button_change_end_time.setOnClickListener {
            var fragment = TimePickerFragment.newInstance(Constants.TIME_PICKER_END)
            fragment.setTargetFragment(this, Constants.TIME_PICKER_END)
            fragment.show(fragmentManager, "timePicker")
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CreateClassInterface) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    private fun createNewClassModel(): ClassModel {
        var newClassModel = ClassModel()
        newClassModel.title = edit_title.text.toString()
        newClassModel.timeStart = Timestamp(timeStart)
        newClassModel.timeEnd = Timestamp(timeEnd)
        newClassModel.days = mutableListOf(1, 2, 3)
        return newClassModel
    }

    fun setTime(tag: Int, time: Long) {
        Timber.d("set time was called " + tag)
        when (tag) {
            Constants.TIME_PICKER_START -> {
                timeStart = time
                text_start_time.text = time.toString()
            }
            Constants.TIME_PICKER_END -> {
                timeEnd = time
                text_end_time.text = time.toString()
            }

        }
    }

}
