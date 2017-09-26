package com.scottquach.homeworkchatbotassistant.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.fragments.DisplayAssignmentsFragment
import com.scottquach.homeworkchatbotassistant.inflate
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import kotlinx.android.synthetic.main.row_assignment.view.*
import timber.log.Timber

/**
 * Created by Scott Quach on 9/22/2017.
 */
class RecyclerAssignmentsAdapter(private var userAssignments:MutableList<AssignmentModel>, fragment: DisplayAssignmentsFragment) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: AssignmentAdapterInterface? = null

    init {
        if (fragment is AssignmentAdapterInterface) {
            listener = fragment
        } else throw RuntimeException(fragment!!.toString() + " must implement DisplayHomeworkInterface")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AssignmentViewHolder(parent.inflate(R.layout.row_assignment))
    }

    override fun getItemCount() = userAssignments.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as AssignmentViewHolder).bindInformation(userAssignments[position])
        (holder as AssignmentViewHolder).itemView.button_delete.setOnClickListener {
            listener?.delete(userAssignments[position].key)
        }
    }

    fun updateData(newData: MutableList<AssignmentModel>) {
        userAssignments.clear()
        userAssignments = newData
        notifyDataSetChanged()
        Timber.d("data updated")
    }

    class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindInformation(model: AssignmentModel) {
            itemView.apply {
                text_assignment_title.text = model.title
                text_assignment_class.text = model.userClass
                text_assignment_due_date.text = model.dueDate
            }
        }
    }

    interface AssignmentAdapterInterface {
        fun delete(key: String)
    }
}