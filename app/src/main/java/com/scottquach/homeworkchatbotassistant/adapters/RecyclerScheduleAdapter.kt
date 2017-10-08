package com.scottquach.homeworkchatbotassistant.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.scottquach.homeworkchatbotassistant.AlertDialogFragment
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import com.scottquach.homeworkchatbotassistant.fragments.DisplayScheduleFragment
import com.scottquach.homeworkchatbotassistant.inflate
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import kotlinx.android.synthetic.main.row_class.view.*
import timber.log.Timber

/**
 * Created by Scott Quach on 9/13/2017.
 */

class RecyclerScheduleAdapter(private var userClassModels: MutableList<ClassModel>, val fragment:DisplayScheduleFragment) :
        RecyclerView.Adapter<RecyclerScheduleAdapter.ViewHolder>() {

    private var listener: ScheduleAdapterInterface? = null

    init {
        if (fragment is DisplayScheduleFragment) {
            listener = fragment
        } else throw RuntimeException(fragment!!.toString() + " must implement DisplayHomeworkInterface")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        return ViewHolder(parent.inflate(R.layout.row_class))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindInformation(userClassModels[holder.adapterPosition])
        holder.itemView.findViewById<ImageView>(R.id.button_class_delete).setOnClickListener {
            listener?.deleteClass(userClassModels[holder.adapterPosition], holder.adapterPosition)
        }
    }

    override fun getItemCount() = userClassModels.size

    public fun removeItem(position: Int) {
       userClassModels.removeAt(position)
       notifyItemRemoved(position)
    }


    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindInformation(model: ClassModel) {
            itemView.apply {
                text_title.text = model.title
                text_time.text = StringUtils.getTimeString(model.timeEnd)
                text_day_display.text = StringUtils.getDaysOfWeek(model.days)
            }
        }
    }

    interface ScheduleAdapterInterface {
        fun deleteClass(model: ClassModel, position: Int)
    }

}
