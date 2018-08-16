package com.scottquach.homeworkchatbotassistant.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.scottquach.homeworkchatbotassistant.database.AssignmentDatabase
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.fragments.DisplayAssignmentsFragment
import com.scottquach.homeworkchatbotassistant.inflate
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import kotlinx.android.synthetic.main.row_assignment.view.*
import timber.log.Timber

/**
 * Created by Scott Quach on 9/22/2017.
 */
class RecyclerAssignmentsAdapter(private val context: Context,
                                 fragment: DisplayAssignmentsFragment) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: AssignmentAdapterInterface? = null
    private var userAssignments = mutableListOf<AssignmentModel>()

    private val TYPE_OVERDUE = 0
    private val TYPE_REGULAR = 1

    init {
        if (fragment is AssignmentAdapterInterface) {
            listener = fragment
        } else throw RuntimeException(fragment!!.toString() + " must implement DisplayHomeworkInterface")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView!!)
        listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_REGULAR -> {
                AssignmentViewHolder(parent.inflate(R.layout.row_assignment))
            }
            TYPE_OVERDUE -> {
                AssignmentDueViewHolder(parent.inflate(R.layout.row_assignment_overdue))
            }
            else -> {
                AssignmentViewHolder(parent.inflate(R.layout.row_assignment))
            }
        }
    }

    override fun getItemCount() = userAssignments.size

    override fun getItemViewType(position: Int): Int {
        return if (AssignmentDatabase.isOverdueAssignment(context, userAssignments[position])) {
            TYPE_OVERDUE
        } else TYPE_REGULAR
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (AssignmentDatabase.isOverdueAssignment(context, userAssignments[position])) {
            (holder as AssignmentDueViewHolder).bindInformation(userAssignments[position])
            (holder as AssignmentDueViewHolder).itemView.findViewById<ImageView>(R.id.button_assignment_delete).setOnClickListener {
                listener?.delete(userAssignments[holder.adapterPosition], holder.adapterPosition)
            }
        } else {
            (holder as AssignmentViewHolder).bindInformation(userAssignments[position])
            (holder as AssignmentViewHolder).itemView.findViewById<ImageView>(R.id.button_assignment_delete).setOnClickListener {
                listener?.delete(userAssignments[holder.adapterPosition], holder.adapterPosition)
            }
        }
    }

    fun add(model: AssignmentModel) {
        userAssignments.add(model)
        Timber.d(userAssignments.toString())
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        userAssignments.removeAt(position)
        notifyItemRemoved(position)
    }

    fun resetData(){
        userAssignments.clear()
        Timber.d(userAssignments.toString())
        notifyDataSetChanged()
    }

    class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindInformation(model: AssignmentModel) {
            itemView.apply {
                text_assignment_title.text = model.title
                text_assignment_class.text = model.userClass
                text_assignment_due_date.text = StringUtils.convertStoredDateToAmericanDate(model.dueDate)
            }
        }
    }

    class AssignmentDueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindInformation(model: AssignmentModel) {
            itemView.apply {
                text_assignment_title.text = model.title
                text_assignment_class.text = model.userClass
                text_assignment_due_date.text = StringUtils.convertStoredDateToAmericanDate(model.dueDate)
            }
        }
    }

    interface AssignmentAdapterInterface {
        fun delete(model: AssignmentModel, position: Int)
    }
}