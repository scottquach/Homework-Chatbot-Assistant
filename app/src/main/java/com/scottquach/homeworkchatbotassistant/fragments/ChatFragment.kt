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

import com.scottquach.homeworkchatbotassistant.adapters.RecyclerChatAdapter
import com.scottquach.homeworkchatbotassistant.contracts.ChatContract
import com.scottquach.homeworkchatbotassistant.models.MessageModel
import com.scottquach.homeworkchatbotassistant.presenters.ChatPresenter
import com.scottquach.homeworkchatbotassistant.utils.AnimationUtils
import com.scottquach.homeworkchatbotassistant.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment(), ChatContract.View {
    override fun scrollToBottom() {
        recycler.scrollToPosition(adapter.itemCount - 1)
    }


    private lateinit var recycler: RecyclerView
    private lateinit var adapter: RecyclerChatAdapter

    private var listener: ChatFragment.ChatInterface? = null

    private lateinit var presenter: ChatPresenter

    interface ChatInterface {
        fun notifyNoInternetConnection()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_chat)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler = recycler_messages
        adapter = RecyclerChatAdapter(context)
        val manager = LinearLayoutManager(context)
        manager.stackFromEnd = true
        recycler.apply {
            adapter = this@ChatFragment.adapter
            layoutManager = manager
        }

        presenter.loadData()

        button_send.setOnClickListener {
            if (NetworkUtils.isConnected(context)) {
                if (edit_input.text.isNotEmpty()) {
                    presenter.onSendMessageButtonClicked()
                }
            } else listener?.notifyNoInternetConnection()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ChatFragment.ChatInterface) {
            listener = context
            presenter = ChatPresenter(this)
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun textNoMessagesSetVisible() {
        text_loading_messages.visibility = View.VISIBLE
    }

    override fun textNoMessagesSetInvisible() {
        text_loading_messages.visibility = View.INVISIBLE
    }

    override fun addData(data: List<MessageModel>) {
        adapter.addData(data)
        adapter.notifyDataSetChanged()
    }

    override fun updateMessages(model: MessageModel) {
        adapter.addMessage(model)
    }

    override fun animateSendButton() {
        AnimationUtils.shrinkGrow(button_send,
                resources.getInteger(android.R.integer.config_shortAnimTime))
    }
}
