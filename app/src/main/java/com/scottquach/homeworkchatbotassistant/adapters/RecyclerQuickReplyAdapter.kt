package com.scottquach.homeworkchatbotassistant.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.fragments.ChatFragment
import com.scottquach.homeworkchatbotassistant.inflate
import kotlinx.android.synthetic.main.quick_reply.view.*

/**
 * Created by Scott Quach on 10/29/2017.
 */
class RecyclerQuickReplyAdapter(val fragment: ChatFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val quickReplies = mutableListOf<String>(fragment.context!!.getString(R.string.quick_reply_next_assignment),
            fragment.context!!.getString(R.string.quick_reply_overdue),
            fragment.context!!.getString(R.string.quick_reply_current_assignments))

    private var listener: QuickReplyInterface? = null

    init {
        if (fragment is ChatFragment) {
            listener = fragment
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as QuickReplyViewHolder).bindInformation(quickReplies[position])
        holder.itemView.text_reply.setOnClickListener {
            listener?.onQuickReply(quickReplies[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return QuickReplyViewHolder(parent.inflate(R.layout.quick_reply))
    }

    override fun getItemCount(): Int {
        return quickReplies.size
    }

    fun add(reply: String) {
        quickReplies.add(reply)
    }

    fun removeItem(reply: String) {
        quickReplies.remove(reply)
    }

    class QuickReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindInformation(reply: String) {
            itemView.text_reply.text = reply
        }
    }

    interface QuickReplyInterface {
        fun onQuickReply(reply: String)
    }
}