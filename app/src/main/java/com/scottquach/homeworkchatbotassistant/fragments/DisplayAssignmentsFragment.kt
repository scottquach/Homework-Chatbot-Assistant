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
    DisplayAssignmentsContract.View{
    override fun addData(data: List<AssignmentModel>) {
        for (model in data) {
            assignmentsAdapter?.add(model)
        }
        assignmentsAdapter?.notifyDataSetChanged()
    }

    override fun removeAssignment(position: Int) {
        assignmentsAdapter?.removeItem(position)
    }

    override fun textNoHomeworkSetVisible() {
        text_no_homework?.visibility = View.VISIBLE
    }

    override fun textNoHomeworkSetInvisible() {
        text_no_homework.visibility = View.INVISIBLE
    }

    private var listener: DisplayHomeworkInterface? = null

    private var assignmentsRecycler: RecyclerView? = null
    private var assignmentsAdapter: RecyclerAssignmentsAdapter? = null

    private lateinit var presenter: DisplayAssignmentsPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_display_assignments)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DisplayHomeworkInterface) {
            listener = context
            presenter = DisplayAssignmentsPresenter(this)
        } else {
            throw RuntimeException(context!!.toString() + " must implement DisplayHomeworkInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        assignmentsRecycler = recycler_homework
        assignmentsAdapter = RecyclerAssignmentsAdapter(context, this)
        assignmentsRecycler?.apply {
            adapter = this@DisplayAssignmentsFragment.assignmentsAdapter
            layoutManager = LinearLayoutManager(this@DisplayAssignmentsFragment.context)
        }

        presenter.loadData()
    }

    override fun delete(model: AssignmentModel, position: Int) {
        presenter.deleteAssignment(model, position)
    }

    interface DisplayHomeworkInterface {
    }
}
