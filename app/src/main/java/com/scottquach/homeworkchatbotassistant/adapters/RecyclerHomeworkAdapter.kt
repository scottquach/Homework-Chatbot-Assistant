package com.scottquach.homeworkchatbotassistant.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.inflate
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import kotlinx.android.synthetic.main.row_assignment.view.*

/**
 * Created by Scott Quach on 9/22/2017.
 */
class RecyclerHomeworkAdapter( private var userAssignments:MutableList<AssignmentModel>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AssignmentViewHolder(parent.inflate(R.layout.row_assignment))
    }

    override fun getItemCount() = userAssignments.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as AssignmentViewHolder).bindInformation(userAssignments[position])
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

}