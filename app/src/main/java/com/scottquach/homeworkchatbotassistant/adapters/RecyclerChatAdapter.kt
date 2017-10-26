package com.scottquach.homeworkchatbotassistant.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.scottquach.homeworkchatbotassistant.MessageType
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.models.MessageModel

import timber.log.Timber

/**
 * Created by Scott Quach on 9/10/2017.
 */

class RecyclerChatAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val userMessages = mutableListOf<MessageModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            MessageType.SENT -> {
                val view = LayoutInflater.from(context).inflate(R.layout.row_message_sent, parent, false)
                return SentViewHolder(view)
            }
            MessageType.RECEIVED -> {
                val view1 = LayoutInflater.from(context).inflate(R.layout.row_message_received, parent, false)
                return ReceivedViewHolder(view1)
            }
            else -> {
                val view2 = LayoutInflater.from(context).inflate(R.layout.row_message_sent, parent, false)
                return SentViewHolder(view2)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            MessageType.SENT -> (holder as SentViewHolder).messageViewSent.text = userMessages[position].message
            MessageType.RECEIVED -> (holder as ReceivedViewHolder).messageViewReceived.text = userMessages[position].message
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (userMessages[position].type.toInt()) {
            MessageType.SENT -> return MessageType.SENT
            MessageType.RECEIVED -> return MessageType.RECEIVED
            else -> {
                Timber.d("couldn't get view type, resorting to default")
                return MessageType.SENT
            }
        }
    }

    override fun getItemCount(): Int {
        return userMessages.size
    }

    fun addMessage(model: MessageModel) {
        userMessages.add(model)
        notifyItemInserted(itemCount)
    }

    fun addData(newData: List<MessageModel>) {
        for (model in newData) {
            userMessages.add(model)
        }
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var messageViewSent: TextView

        init {
            messageViewSent = itemView.findViewById(R.id.view_message_sent)
        }
    }


    inner class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var messageViewReceived: TextView

        init {
            messageViewReceived = itemView.findViewById(R.id.view_message_received)
        }
    }
}
