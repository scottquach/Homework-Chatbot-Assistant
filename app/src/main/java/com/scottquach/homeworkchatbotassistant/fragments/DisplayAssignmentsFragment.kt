package com.scottquach.homeworkchatbotassistant.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.scottquach.homeworkchatbotassistant.*

import com.scottquach.homeworkchatbotassistant.adapters.RecyclerAssignmentsAdapter
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import kotlinx.android.synthetic.main.fragment_display_assignments.*

class DisplayAssignmentsFragment : Fragment(), RecyclerAssignmentsAdapter.AssignmentAdapterInterface {

    private var listener: DisplayHomeworkInterface? = null

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var userAssignments = mutableListOf<AssignmentModel>()

    private var assignmentsRecycler: RecyclerView? = null

    private var assignmentsAdapter: RecyclerAssignmentsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_display_assignments)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DisplayHomeworkInterface) {
            listener = context
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

        userAssignments.clear()
        userAssignments = BaseApplication.getInstance().database.getAssignments().toMutableList()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
            assignmentsAdapter = RecyclerAssignmentsAdapter(context, userAssignments, this@DisplayAssignmentsFragment)
            assignmentsRecycler?.apply {
                adapter = this@DisplayAssignmentsFragment.assignmentsAdapter
                layoutManager = LinearLayoutManager(this@DisplayAssignmentsFragment.context)
            }

        if (text_no_homework?.visibility == View.VISIBLE && !userAssignments.isEmpty()) {
            text_no_homework?.visibility = View.INVISIBLE
        }
    }

    override fun delete(model: AssignmentModel, position: Int) {
        databaseReference.child("users").child(user!!.uid).child("assignments").child(model.key).removeValue()
        assignmentsAdapter?.removeItem(position)

        val manager = NotifyClassEndManager(context)
        manager.startManaging()
    }

    interface DisplayHomeworkInterface {
    }
}
