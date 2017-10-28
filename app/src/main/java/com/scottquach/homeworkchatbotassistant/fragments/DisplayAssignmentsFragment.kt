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

import com.scottquach.homeworkchatbotassistant.adapters.RecyclerAssignmentsAdapter
import com.scottquach.homeworkchatbotassistant.contracts.DisplayAssignmentsContract
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.presenters.DisplayAssignmentsPresenter
import kotlinx.android.synthetic.main.fragment_display_assignments.*

class DisplayAssignmentsFragment : Fragment(), RecyclerAssignmentsAdapter.AssignmentAdapterInterface,
        DisplayAssignmentsContract.View {

    override fun addData(data: List<AssignmentModel>) {
        for (model in data) {
            assignmentsAdapter?.add(model)
        }
        assignmentsAdapter?.notifyDataSetChanged()
    }

    override fun removeAssignment(position: Int) {
        assignmentsAdapter?.removeItem(position)
    }

    override fun toggleNoHomeworkLabelsVisible() {
        text_no_homework.visibility = View.VISIBLE
        image_no_homework.visibility = View.VISIBLE
    }

    override fun toggleNoHomeworkLabelsInvisible() {
        text_no_homework.visibility = View.INVISIBLE
        image_no_homework.visibility = View.INVISIBLE
    }

    private var assignmentsRecycler: RecyclerView? = null
    private var assignmentsAdapter: RecyclerAssignmentsAdapter? = null

    private lateinit var presenter: DisplayAssignmentsPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_display_assignments)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter = DisplayAssignmentsPresenter(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        assignmentsRecycler = recycler_homework
        assignmentsAdapter = RecyclerAssignmentsAdapter(context, this)
        assignmentsRecycler?.apply {
            adapter = this@DisplayAssignmentsFragment.assignmentsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        presenter.loadData()
    }

    override fun delete(model: AssignmentModel, position: Int) {
        presenter.deleteAssignment(model, position)
    }

    /**
     * Notifies the user that they cannot proceed until a stable internet connection is established
     */
    override fun notifyNoInternet() {
        AlertDialogFragment.newInstance(getString(R.string.no_internet_connection),
                getString(R.string.cannot_send_messages_internet_connection), positiveString = "Ok",haveNegative = false)
                .show(fragmentManager, AlertDialogFragment::class.java.name)
    }

}
