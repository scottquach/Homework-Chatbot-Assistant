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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.contracts.CreateClassContract
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import com.scottquach.homeworkchatbotassistant.presenters.CreateClassPresenter
import com.scottquach.homeworkchatbotassistant.utils.NetworkUtils
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_create_class.*
import timber.log.Timber

class CreateClassFragment : Fragment(), CreateClassContract.View {

    private var listener: CreateClassInterface? = null

    private lateinit var presenter: CreateClassPresenter

    interface CreateClassInterface {
        fun switchToDisplayFragment()
        fun notifyNoInternetConnection()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_create_class)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        floating_confirm.setOnClickListener {
            presenter.onCreateClassAttempt()
        }

        floating_cancel.setOnClickListener {
            listener?.switchToDisplayFragment()
        }

        button_change_end_time.setOnClickListener {
            presenter.onPickEndTime()
        }

        button_day_picker.setOnClickListener {
            presenter.onPickDay()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CreateClassInterface) {
            listener = context
            presenter = CreateClassPresenter(this)
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun updateDayOfWeekView(message: String) {
        text_day_display.text = message
    }

    override fun updateEndTimeView(message: String) {
        text_end_time.text = message
    }

    override fun showEndTimePicker() {
        val fragment = TimePickerFragment.newInstance(Constants.TIME_PICKER_END)
        fragment.setTargetFragment(this, Constants.TIME_PICKER_END)
        fragment.show(fragmentManager, "timePicker")
    }

    /**
     * Allows users to choose what day they want a class to occur from Sunday to Saturday
     */
    override fun showDayPicker() {
        val items: Array<String> = resources.getStringArray(R.array.days_of_week)

        AlertDialog.Builder(context)
                .setTitle(getString(R.string.select_class_days_dialog_title))
                .setMultiChoiceItems(items, null, { dialogInterface: DialogInterface?, index: Int, isChecked: Boolean ->
                    presenter.onToggleDayPicked(isChecked, index)
                })
                .setPositiveButton(getString(R.string.set), { dialogInterface, i ->
                    presenter.setDaysOfWeek()
                })
                .setNegativeButton(getString(R.string.cancel), { dialogInterface, i -> })
                .create().show()
    }

    override fun notifyNoInternet() {
        AlertDialogFragment.newInstance(getString(R.string.no_internet_connection),
                getString(R.string.cannot_send_messages_internet_connection), positiveString = getString(R.string.ok), haveNegative = false)
                .show(fragmentManager, AlertDialogFragment::class.java.name)
    }

    override fun notifyClassCreated() {
        Toast.makeText(context, getString(R.string.class_created), Toast.LENGTH_SHORT).show()
    }

    override fun notifyMissingRequiredFields() {
        Toast.makeText(context, getString(R.string.missing_required_fields), Toast.LENGTH_SHORT).show()
    }

    fun returnToScheduleView() {
        listener?.switchToDisplayFragment()
    }

    /**
     * Called through the time picker fragment
     */
    fun setEndTime(tag: Int, time: TimeModel) {
        presenter.setEndTime(time)
    }
}
