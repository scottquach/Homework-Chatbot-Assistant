package com.scottquach.homeworkchatbotassistant.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.adapters.RecyclerScheduleAdapter
import com.scottquach.homeworkchatbotassistant.contracts.DisplayScheduleContract
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.presenters.DisplaySchedulePresenter
import kotlinx.android.synthetic.main.fragment_display_schedule.*
import timber.log.Timber

/**
 * Handles displaying user classes to the user and editing options.
 * Activity must implement ScheduleDisplayListener to handle fragment transitions
 */
class DisplayScheduleFragment : Fragment(), RecyclerScheduleAdapter.ScheduleAdapterInterface,
        DisplayScheduleContract.View {
    override fun textNoAssignmentSetVisible() {
        text_no_classes.visibility = View.VISIBLE
    }

    override fun textNoAssignmentSetInvisible() {
        text_no_classes.visibility = View.INVISIBLE
    }

    override fun removeClass(position: Int) {
        scheduleAdapter?.removeItem(position)
    }

    override fun addData(data: List<ClassModel>) {
        scheduleAdapter?.add(data)
        scheduleAdapter?.notifyDataSetChanged()
    }

    private var listener: ScheduleDisplayInterface? = null

    private var scheduleRecycler: RecyclerView? = null
    private var scheduleAdapter: RecyclerScheduleAdapter? = null

    private lateinit var presenter: DisplaySchedulePresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ScheduleDisplayInterface) {
            listener = context
            presenter = DisplaySchedulePresenter(this)
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_display_schedule)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        scheduleRecycler = recycler_schedule
        scheduleAdapter = RecyclerScheduleAdapter(this@DisplayScheduleFragment)
        scheduleRecycler?.apply {
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(context)
        }

        presenter.loadData()

        floating_create_class.setOnClickListener {
            listener?.switchToCreateFragment()
        }
    }

    interface ScheduleDisplayInterface {
        fun switchToCreateFragment()
    }

    override fun deleteClass(model: ClassModel, position: Int) {
        presenter.deleteClass(context, model, position)
    }
}
