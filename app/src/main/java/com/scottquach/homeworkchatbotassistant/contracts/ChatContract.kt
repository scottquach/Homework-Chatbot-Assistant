package com.scottquach.homeworkchatbotassistant.contracts

import ai.api.model.Result
import com.scottquach.homeworkchatbotassistant.models.MessageModel

/**
 * Created by Scott Quach on 10/25/2017.
 */
interface ChatContract {
    interface View {
        fun textNoMessagesSetVisible()
        fun textNoMessagesSetInvisible()
        fun addData(data: List<MessageModel>)
        fun updateMessages(model: MessageModel)
        fun animateSendButton()
        fun scrollToBottom()
        fun notifyNoInternet()
    }

    interface Presenter {
        fun loadData()
        fun messagesLoaded()
        fun determineResponseActions(result: Result)
        fun onMessageAdded(model: MessageModel)
//        fun setDefaultContext()
        fun onSendMessageButtonClicked()
        fun addMessage(message: String)
    }
}