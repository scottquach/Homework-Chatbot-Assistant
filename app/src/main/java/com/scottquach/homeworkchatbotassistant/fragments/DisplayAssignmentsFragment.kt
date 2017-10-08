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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager

import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.adapters.RecyclerAssignmentsAdapter
import com.scottquach.homeworkchatbotassistant.inflate
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import kotlinx.android.synthetic.main.fragment_display_assignments.*
import timber.log.Timber

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
        loadData()
    }

    private fun loadData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Timber.d("Data Changed called")
                compileData(p0)
            }

            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("Database Error " + p0.toString())
            }
        })
    }


    private fun compileData(dataSnapshot: DataSnapshot) {
        userAssignments.clear()
        for (ds in dataSnapshot.child("users").child(user!!.uid).child("assignments").children) {
            var model = AssignmentModel()
            model.title = ds.child("title").value as String
            model.userClass = ds.child("userClass").value as String
            model.dueDate = ds.child("dueDate").value as String
            model.scale = (ds.child("scale").value as Long).toInt()
            model.key = ds.child("key").value as String
            Timber.d("assignment model " + model.toString())
            userAssignments.add(model)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
            assignmentsAdapter = RecyclerAssignmentsAdapter(userAssignments, this@DisplayAssignmentsFragment)
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
