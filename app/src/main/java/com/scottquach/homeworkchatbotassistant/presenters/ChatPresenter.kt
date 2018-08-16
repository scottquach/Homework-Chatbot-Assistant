package com.scottquach.homeworkchatbotassistant.presenters

import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.contracts.ChatContract
import com.scottquach.homeworkchatbotassistant.database.ChatDatabase
import com.scottquach.homeworkchatbotassistant.database.MessageDatabaseHandler
import com.scottquach.homeworkchatbotassistant.fragments.ChatFragment
import com.scottquach.homeworkchatbotassistant.models.MessageModel
import com.scottquach.homeworkchatbotassistant.utils.InstrumentationUtils
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * Created by Scott Quach on 10/25/2017.
 */
class ChatPresenter(val view: ChatFragment) : ChatContract.Presenter, MessageDatabaseHandler.CallbackInterface,
ChatDatabase.CallbackInterface{

    private val userMessages = mutableListOf<MessageModel>()

    private val messageHandler by lazy {
        MessageDatabaseHandler(view.context!!, this)
    }

    private val chatDatabase by lazy {
        ChatDatabase(view.context!!, this)
    }

    private lateinit var convoContext: String
    private lateinit var classContext: String

    override fun loadData() {
        chatDatabase.loadMessages()
    }

    override fun chatDatabaseCallback(data: List<MessageModel>) {
        userMessages.clear()
        userMessages.addAll(data)
        convoContext = BaseApplication.getInstance().database.getConvoContext()
        classContext = BaseApplication.getInstance().database.getClassContext()

        view.addData(userMessages)

        if (userMessages.size > 0) {
            view.textNoMessagesSetInvisible()
        } else view.textNoMessagesSetVisible()
    }

    override fun messagesCallback(model: MessageModel) {
        view.updateMessages(model)
        view.scrollToBottom()
    }

    override fun onSendMessageButtonClicked() {
        view.animateSendButton()

        val text = view.edit_input.text.toString().trim()
        messageHandler.addMessage(MessageType.SENT, text)
        messageHandler.processNewMessage(text)
        view.edit_input.setText("")
        logEvent(InstrumentationUtils.USER_SENT_MESSAGE)
    }

    override fun addMessage(message: String) {
        messageHandler.addMessage(MessageType.SENT, message)
        messageHandler.processNewMessage(message)
    }
}