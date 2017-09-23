package com.scottquach.homeworkchatbotassistant.fragments


import ai.api.AIConfiguration
import ai.api.RequestExtras
import ai.api.android.AIService
import ai.api.model.AIContext
import ai.api.model.AIResponse
import ai.api.model.Result
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.*


import com.scottquach.homeworkchatbotassistant.adapters.RecyclerChatAdapter
import com.scottquach.homeworkchatbotassistant.models.MessageModel
import kotlinx.android.synthetic.main.fragment_chat.*
import timber.log.Timber
import java.sql.Timestamp
import java.util.ArrayList

class ChatFragment : Fragment() {

    private lateinit var aiService: AIService

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    private val messageHandler by lazy {
        MessageHandler()
    }

    private val recycler by lazy {
        recycler_messages
    }
    private lateinit var adapter: RecyclerChatAdapter

    private var userMessages = mutableListOf<MessageModel>()
    private lateinit var convoContext: String
    private lateinit var classContext: String

    private var listener: ChatFragment.ChatInterface? = null

    interface ChatInterface {
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_chat)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val config = ai.api.android.AIConfiguration("35b6e6bf57cf4c6dbeeb18b1753471ab",
                AIConfiguration.SupportedLanguages.English,
                ai.api.android.AIConfiguration.RecognitionEngine.System)
        aiService = AIService.getService(context, config)
//        aiService.setListener(context)

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                loadData(p0)
            }

            override fun onCancelled(p0: DatabaseError?) {
                Timber.d("Error loading data " + p0?.toString())
            }
        })

        button_send.setOnClickListener {
            val text = edit_input.text.toString().trim({ it <= ' ' })
            addMessage(MessageType.SENT, text)
            DoTextRequestTask().execute(text)
            edit_input.setText("")
        }

        button4.setOnClickListener {
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ChatFragment.ChatInterface) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun loadData(dataSnapshot: DataSnapshot) {
        userMessages.clear()
        for (ds in dataSnapshot.child("users").child(user!!.uid).child("messages").children) {
            val messageModel = MessageModel()
            messageModel.type = ds.child("type").value as Long
            messageModel.message = ds.child("message").value as String
            messageModel.timestamp = Timestamp((ds.child("timestamp").child("time").value as Long))
            userMessages.add(messageModel)
        }
        convoContext = dataSnapshot.child("users").child(user.uid).child("contexts").child("conversation").value as String
        classContext = dataSnapshot.child("users").child(user.uid).child("contexts").child("class").value as String
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = RecyclerChatAdapter(userMessages, context)
        val manager = LinearLayoutManager(context)
        manager.stackFromEnd = true
        recycler.apply {
            adapter = this@ChatFragment.adapter
            layoutManager = manager
        }
    }

    private fun addMessage(messageType: Int, message: String) {
        val key = databaseReference.child("users").child(user!!.uid).child("messages").push().key

        val model = MessageModel(messageType.toLong(), message, Timestamp(System.currentTimeMillis()), key)
        userMessages.add(model)
        adapter.addMessage(model)

        databaseReference.child("users").child(user!!.uid).child("messages").child(key).setValue(model)
    }

    private fun defaultContext() {
        databaseReference.child("users").child(user!!.uid).child("contexts").child("conversation")
                .setValue(Constants.CONETEXT_DEFAULT)
    }

    private fun determineResponseActions(result: Result) {
        when (result.action) {
            Constants.ACTION_ASSIGNMENT_SPECIFIC_CLASS -> Timber.d("Action was specific class")
            Constants.ACTION_ASSIGNMENT_PROMPTED_CLASS -> {
                Timber.d("Action was prompted class")
                val params = result.parameters
                val date = params["date"]!!.asString
                val assignment = params["assignment-official"]!!.asString
                Timber.d("Received words were $date $assignment")
                messageHandler.confirmNewHomework(assignment, classContext, date)
                defaultContext()
            }
            else -> {
                val textResponse = result.fulfillment.speech
                addMessage(MessageType.RECEIVED, textResponse)
            }
        }
    }

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
