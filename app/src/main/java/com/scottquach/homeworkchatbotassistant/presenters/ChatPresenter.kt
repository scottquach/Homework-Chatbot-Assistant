package com.scottquach.homeworkchatbotassistant.presenters

import ai.api.AIConfiguration
import ai.api.RequestExtras
import ai.api.android.AIService
import ai.api.model.AIContext
import ai.api.model.AIResponse
import ai.api.model.Result
import android.os.AsyncTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.contracts.ChatContract
import com.scottquach.homeworkchatbotassistant.fragments.ChatFragment
import com.scottquach.homeworkchatbotassistant.models.MessageModel
import kotlinx.android.synthetic.main.fragment_chat.*
import timber.log.Timber
import java.util.ArrayList

/**
 * Created by Scott Quach on 10/25/2017.
 */
class ChatPresenter(val view: ChatFragment) : ChatContract.Presenter {


    private val aiService: AIService

    private lateinit var userMessages: MutableList<MessageModel>

    private val messageHandler by lazy {
        MessageHandler(view.context, this)
    }

    private lateinit var convoContext: String
    private lateinit var classContext: String

    init {
        val config = ai.api.android.AIConfiguration("35b6e6bf57cf4c6dbeeb18b1753471ab",
                AIConfiguration.SupportedLanguages.English,
                ai.api.android.AIConfiguration.RecognitionEngine.System)
        aiService = AIService.getService(view.context, config)
    }

    override fun loadData() {
        messageHandler.loadMessages()
    }

    override fun messagesLoaded() {
        userMessages = messageHandler.getMessages().toMutableList()
        convoContext = BaseApplication.getInstance().database.getConvoContext()
        classContext = BaseApplication.getInstance().database.getClassContext()

        view.addData(userMessages)

        if (userMessages.size > 0) {
            view.textNoMessagesSetInvisible()
        } else view.textNoMessagesSetVisible()
    }

    override fun onSendMessageButtonClicked() {
        view.animateSendButton()

        val text = view.edit_input.text.toString().trim()
        messageHandler.addMessage(MessageType.SENT, text)
        DoTextRequestTask().execute(text)
        view.edit_input.setText("")
    }

    override fun onMessageAdded(model: MessageModel) {
        view.updateMessages(model)
        view.scrollToBottom()
    }

    override fun addMessage(message: String) {
        messageHandler.addMessage(MessageType.SENT, message)
        DoTextRequestTask().execute(message)
    }

    override fun determineResponseActions(result: Result) {
        when (result.action) {
            Constants.ACTION_ASSIGNMENT_SPECIFIC_CLASS -> {
                Timber.d("Action was specific class")
                val params = result.parameters
                val date = params["date"]?.asString?.trim()
                val assignment = params["assignment-official"]?.asString?.trim()
                val userClass = params["class"]?.asString?.trim()

                if (date.isNullOrEmpty() || assignment.isNullOrEmpty() || userClass.isNullOrEmpty()) {
                    val textResponse = result.fulfillment.speech
                    messageHandler.addMessage(MessageType.RECEIVED, textResponse)
                } else {
                    messageHandler.confirmNewAssignmentSpecificClass(assignment!!, userClass!!, date!!)
                }
            }
            Constants.ACTION_ASSIGNMENT_PROMPTED_CLASS -> {
                Timber.d("Action was prompted class")
                val params = result.parameters
                val date = params["date"]?.asString?.trim()
                val assignment = params["assignment-official"]?.asString?.trim()

                if (date.isNullOrEmpty() || assignment.isNullOrEmpty()) {
                    val textResponse = result.fulfillment.speech
                    messageHandler.addMessage(MessageType.RECEIVED, textResponse)
                } else {
                    messageHandler.confirmNewAssignment(assignment!!, classContext, date!!)
                }
//                defaultContext()
            }
            Constants.ACTION_OVERDUE_ASSIGNMENTS -> {
                messageHandler.getOverdueAssignments(view.context)
            }
            Constants.ACTION_NEXT_ASSIGNMENT -> {
                messageHandler.getNextAssignment(view.context)
            }
            Constants.ACTION_REQUEST_HELP -> {
                messageHandler.receiveHelp()
            }
            Constants.ACTION_CURRENT_ASSIGNMENTS -> {
                messageHandler.getCurrentAssignments(view.context)
            }
            Constants.ACTION_EXAMPLE -> {
                messageHandler.getExamples(view.context)
            }
            else -> {
                val textResponse = result.fulfillment.speech
                messageHandler.addMessage(MessageType.RECEIVED, textResponse)
            }
        }
    }

//    override fun setDefaultContext() {
//        databaseReference.child("users").child(user!!.uid).child("contexts").child("conversation")
//                .setValue(Constants.CONETEXT_DEFAULT)
//    }

    internal inner class DoTextRequestTask : AsyncTask<String, Void, AIResponse>() {
        private val exception: Exception? = null

        override fun doInBackground(vararg text: String): AIResponse? {
            var resp: AIResponse? = null
            try {
                resp = run {
                    val contexts = ArrayList<AIContext>()
                    contexts.add(AIContext(convoContext))
                    Timber.d("context is " + convoContext)
                    val requestExtras = RequestExtras(contexts, null)
                    aiService.textRequest(text[0], requestExtras)
                }
            } catch (e: Exception) {
                Timber.d(e)
            }
            return resp
        }

        override fun onPostExecute(response: AIResponse?) {
            if (response != null && !response.isError) {
                val result = response.result

                val params = result.parameters
                if (params != null && !params.isEmpty()) {
                    for ((key, value) in params) {
                        Timber.d(String.format("%s: %s", key, value.toString()))
                    }
                }

                Timber.d("Query:" + result.resolvedQuery +
                        "\nAction: " + result.action)
                determineResponseActions(result)
            } else {
                Timber.d("API.AI response was an error ")
            }
        }
    }

}