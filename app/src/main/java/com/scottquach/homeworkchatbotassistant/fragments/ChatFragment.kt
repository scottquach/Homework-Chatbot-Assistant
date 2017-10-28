package com.scottquach.homeworkchatbotassistant.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
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

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: RecyclerChatAdapter


    private lateinit var presenter: ChatPresenter

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
            } else notifyNoInternet()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
            presenter = ChatPresenter(this)
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

    override fun scrollToBottom() {
        recycler.scrollToPosition(adapter.itemCount - 1)
    }

    /**
     * Notifies the user that they cannot proceed until a stable internet connection is established
     */
    override fun notifyNoInternet() {
        AlertDialogFragment.newInstance(getString(R.string.no_internet_connection),
                getString(R.string.cannot_send_messages_internet_connection), positiveString = "Ok",haveNegative = false)
                .show(fragmentManager, AlertDialogFragment::class.java.name)
    }

}
