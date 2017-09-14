package com.scottquach.homeworkchatbotassistant.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.inflate
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import kotlinx.android.synthetic.main.row_class.view.*

/**
 * Created by Scott Quach on 9/13/2017.
 */

class RecyclerScheduleAdapter(var userClassModels: MutableList<ClassModel>) : RecyclerView.Adapter<RecyclerScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        return ViewHolder(parent.inflate(R.layout.row_class))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindInformation(userClassModels[position])
    }

    override fun getItemCount(): Int {
        return userClassModels.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindInformation(model: ClassModel) {
            itemView.text_title.text = model.title
        }
    }


}
