package com.scottquach.homeworkchatbotassistant

import ai.api.AIConfiguration
import ai.api.RequestExtras
import ai.api.android.AIService
import ai.api.model.AIContext
import ai.api.model.AIResponse
import ai.api.model.Result
import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.database.AssignmentDatabaseManager
import com.scottquach.homeworkchatbotassistant.database.BaseDatabase
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.models.MessageModel
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import timber.log.Timber

import java.sql.Timestamp
import java.util.ArrayList

/**
 * Created by Scott Quach on 9/15/2017.
 *
 * Responsible for pushing new messages to the database, if a presenter is present it also
 * notifies the presenter of these changes in order to update the UI
 */

class MessageHandler(val context: Context, caller: Any) : BaseDatabase() {

    private val aiService: AIService

    private var listener: CallbackInterface? = null

    interface CallbackInterface {
        fun messagesCallback(model: MessageModel)
    }

    init {
        if (caller is CallbackInterface) {
            listener = caller as CallbackInterface
        }
        val config = ai.api.android.AIConfiguration("35b6e6bf57cf4c6dbeeb18b1753471ab",
                AIConfiguration.SupportedLanguages.English,
                ai.api.android.AIConfiguration.RecognitionEngine.System)
        aiService = AIService.getService(context, config)
    }

    /**
     * Adds a list of message models to the database
     *
     * @param delayResponse determines whether to delay the response to mimic a more natural experience
     */
    private fun saveMessagesToDatabase(messageModels: List<MessageModel>, delayResponse: Boolean = true) {
        for (model in messageModels) {
            databaseReference.child("users").child(user!!.uid).child("messages").child(model.key).setValue(model)

            if (delayResponse) {
                if (model.type.toInt() == MessageType.RECEIVED) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        listener?.messagesCallback(model)
                    }, 500)
                } else listener?.messagesCallback(model)
            } else listener?.messagesCallback(model)
        }
    }

    fun updateClassContext(classContext: String) {
        databaseReference.child("users").child(user!!.uid).child("contexts")
                .child("class").setValue(classContext)
    }

    fun updateConversationContext(conversationContext: String) {
        databaseReference.child("users").child(user!!.uid).child("contexts")
                .child("conversation").setValue(conversationContext)
    }

    fun updateAssignmentContext(assignmentContext: String) {
        databaseReference.child("users").child(user!!.uid).child("contexts")
                .child("assignment").setValue(assignmentContext)
    }

    fun getClassContext() : String {
        return BaseApplication.getInstance().database.getClassContext()
    }

    fun getConversationContext() : String {
        return BaseApplication.getInstance().database.getConvoContext()
    }

    fun getAssignmentContext() : String {
        return "Default"
    }

    /**
     * Adds a single message from the message string and messageType
     */
    fun addMessage(messageType: Int, message: String) {
        val key = databaseReference.child("users").child(user!!.uid).child("messages").push().key
        val model = MessageModel(messageType.toLong(), message, Timestamp(System.currentTimeMillis()), key)
        saveMessagesToDatabase(listOf(model))
    }

    /**
     * Sends the message to DialogFlow for processing in order to receive an appropriate response
     */
    fun processNewMessage(message: String) {
        DoTextRequestTask().execute(message)
    }

    /**
     * Create and push the messages shown when the user opens the chat for the
     * first time
     */
    fun receiveWelcomeMessages() {
        val message1 = "Hi there I'm ${context.getString(R.string.assistant_name)}, here to help " +
                "organize and assist you in your busy school life!"
        val model1 = createReceivedMessage(message1)

        //Don't yell at the user LOL
        val message2 = "Before I can do my job, please set your classes in the Classes tab! I won't function properly " +
                "without your class information"
        val model2 = createReceivedMessage(message2)

        val message3 = "If you have any questions feel free to ask me for help or even examples! "
        val model3 = createReceivedMessage(message3)

        val message4 = "Otherwise, welcome and thanks for giving me a try! "
        val model4 = createReceivedMessage(message4)

        Timber.d("created welcome message")
        saveMessagesToDatabase(listOf(model1, model2, model3, model4))
    }

    /**
     * Creates and pushes default messages that provides the user with a short tutorial for help
     */
    private fun receiveHelp() {
        val stringMessages = arrayOf(
                "If you haven't done so please specify your classes in the classes tab",
                "Every time you finish a class, I'll ask you what homework you have",
                "And you can tell me by saying something like:",
                "\"I have a chapter 3 summary due next Monday\"",
                "or",
                "\"I have an exam in 4 days\"",
                "Or if you don't have any homework you don't have to say anything.",
                "You can also add assignments to specific classes later by saying something such as: \"Interview Bob for Research Writing by October 5th.",
                "Remember that the above statements are just some examples, feel free to speak the way YOU would naturally speak and I'll learn over time!",
                "Feel free to ask for more examples if you are still confused!")
        val messagesModels = stringMessages.map { createReceivedMessage(it) }
        saveMessagesToDatabase(messagesModels)
        logEvent(InstrumentationUtils.REQUEST_HELP)
    }

    fun assignmentDueReminder(userAssignment: String, userClass: String) {
        val model = MessageModel()
        model.message = "\"$userAssignment\" is due tomorrow for $userClass"
        model.type = MessageType.RECEIVED.toLong()
        model.key = getMessageKey()
        model.timestamp = Timestamp(System.currentTimeMillis())

        saveMessagesToDatabase(listOf(model))
        updateAssignmentContext(userAssignment)
        updateClassContext(userClass)
    }

    fun promptForAssignment(userClass: String): List<MessageModel> {
        val model = MessageModel()
        model.message = "What was the homework for $userClass?"
        model.type = MessageType.RECEIVED.toLong()
        model.key = getMessageKey()
        model.timestamp = Timestamp(System.currentTimeMillis())

        saveMessagesToDatabase(listOf(model))
        updateConversationContext(Constants.CONTEXT_PROMPT_HOMEWORK)
        updateClassContext(userClass)
        return listOf(model)
    }

    fun promptCouldntFindClass(userClass: String) {
        val model = MessageModel()
        model.message = "Couldn't find class $userClass"
        model.type = MessageType.RECEIVED.toLong()
        model.key = getMessageKey()
        model.timestamp = Timestamp(System.currentTimeMillis())

        saveMessagesToDatabase(listOf(model))
    }

    /**
     * Confirm if the information is correct for specific class addition assignments. If it is not
     * notify the user, if it is confirm with the user
     */
    private fun confirmNewAssignmentSpecificClass(assignment: String, userClass: String, dueDate: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                var classMatch = false
                for (ds in dataSnapshot!!.child("users").child(user!!.uid).child("classes").children) {
                    if ((ds.child("title").value as String).equals(userClass, true)) {
                        confirmNewAssignment(assignment, userClass, dueDate)
                        classMatch = true
                        break
                    }
                }
                if (!classMatch) {
                    promptCouldntFindClass(userClass)
                }
            }

            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("Error loading data " + p0.toString())
            }
        })
    }

    /**
     * Send confirmation message so the user knows that the assignment was saved
     */
    fun confirmNewAssignment(assignment: String, userClass: String,
                             dueDate: String) {

        val model = createReceivedMessage("Assignment \"$assignment\" for $userClass on " +
                "${StringUtils.convertStoredDateToAmericanDate(dueDate)} saved")

        val assignmentKey = getAssignmentKey()
        val assignmentModel = AssignmentModel(assignment, userClass.capitalize(), 0, dueDate, assignmentKey)
        databaseReference.child("users").child(user!!.uid).child("assignments")
                .child(assignmentKey).setValue(assignmentModel)
        val assignmentManager = AssignmentDueManager(context)
        assignmentManager.scheduleNextAlarm(assignmentModel)

        saveMessagesToDatabase(listOf(model))
        updateAssignmentContext(assignment)
        updateClassContext(userClass)
    }

    /**
     * Creates a message model that specifies the next due assignment and saves it to the database,
     * has a default message if no upcoming assignments
     */
    private fun getNextAssignment(context: Context) {
        val assignmentManager = AssignmentDatabaseManager(this)
        val nextAssignment = assignmentManager.getNextAssignment(context)

        if (nextAssignment.key == "empty") {
            val messageModel = createReceivedMessage("You don't have any upcoming assignments")
            saveMessagesToDatabase(listOf(messageModel))
        } else {
            val messageModel = createReceivedMessage("Next assignment is \"${nextAssignment.title}\" for ${nextAssignment.userClass} due " +
                    "on ${StringUtils.convertStoredDateToAmericanDate(nextAssignment.dueDate)}")
            saveMessagesToDatabase(listOf(messageModel))
            updateAssignmentContext(nextAssignment.title)
            updateClassContext(nextAssignment.userClass)
        }
        logEvent(InstrumentationUtils.REQUEST_NEXT_ASSIGNMENT)
    }

    /**
     * Creates message models that specify overdue assignments and saves it to the database,
     * has a default message if no overdue assignments
     */
    private fun getOverdueAssignments(context: Context) {
        val assignmentManager = AssignmentDatabaseManager(this)
        val overdueAssignments = assignmentManager.getOverdueAssignments(context)

        if (overdueAssignments.isEmpty()) {
            val messageModel = createReceivedMessage("You have no overdue assignments!")
            saveMessagesToDatabase(listOf(messageModel))
        } else {
            val messages = mutableListOf<MessageModel>()
            var assignmentNumber = 1
            for (assignment in overdueAssignments) {
                val messageModel = createReceivedMessage("$assignmentNumber. \"${assignment.title}\"")
                assignmentNumber++
                messages.add(messageModel)
            }

            saveMessagesToDatabase(messages)
        }
        logEvent(InstrumentationUtils.REQUEST_OVERDUE_ASSIGNMENTS)
    }

    /**
     * Creates message models that specify what assignments are currently not finished, this
     * includes assignments that overdue (specially labeled). If there are not assignments currently
     * available a default message is displayed
     */
    private fun getCurrentAssignments(context: Context) {
        val assignmentManager = AssignmentDatabaseManager(this)
        val userAssignments = assignmentManager.getCurrentAssignments(context)

        if (userAssignments.isEmpty()) {
            val messageModel = createReceivedMessage("You currently have no assignments!")
            saveMessagesToDatabase(listOf(messageModel))
        } else {
            val messages = mutableListOf<MessageModel>()
            var assignmentNumber = 1
            for (assignment in userAssignments) {

                if (AssignmentDatabaseManager.isOverdueAssignment(context, assignment)) {
                    val messageModel = createReceivedMessage("$assignmentNumber. \"${assignment.title}\" (Overdue)")
                    messages.add(messageModel)
                } else {
                    val messageModel = createReceivedMessage("$assignmentNumber. \"${assignment.title}\"")
                    messages.add(messageModel)
                }
                assignmentNumber++
            }
            saveMessagesToDatabase(messages)
        }
        logEvent(InstrumentationUtils.REQUEST_CURRENT_ASSIGNMENTS)
    }

    /**
     * Creates and send message models that provide the user with examples of the apps
     * functionality
     */
    private fun getExamples(context: Context) {
        val message1 = "Provide homework for Calculus III"
        val model1 = createReceivedMessage(message1)

        val message2 = "Finish online review by next Tuesday"
        val model2 = createSentMessage(message2)

        val message3 = "Assignment \"Finish online review\" for Calculus III by 2017-10-14 saved"
        val model3 = createReceivedMessage(message3)

        val message4 = "Study the integral test by Sunday"
        val model4 = createSentMessage(message4)

        val message5 = "Assignment \"Study the integral test\" for Calculus III by 2017-10-13 saved"
        val model5 = createReceivedMessage(message5)

        val message6 = "Research machine learning for Research Writing by October 4th"
        val model6 = createSentMessage(message6)

        val message7 = "Assignment \"Research machine learning\" for Research Writing by 2017-10-04 saved"
        val model7 = createReceivedMessage(message7)

        saveMessagesToDatabase(listOf(model1, model2, model3, model4, model5, model6, model7), false)
        logEvent(InstrumentationUtils.REQUEST_EXAMPLES)
    }

    private fun createSentMessage(message: String): MessageModel {
        val model = MessageModel()
        model.message = message
        model.type = MessageType.SENT.toLong()
        model.timestamp = Timestamp(System.currentTimeMillis())
        model.key = getMessageKey()
        return model
    }

    private fun createReceivedMessage(message: String): MessageModel {
        val model = MessageModel()
        model.message = message
        model.type = MessageType.RECEIVED.toLong()
        model.timestamp = Timestamp(System.currentTimeMillis())
        model.key = getMessageKey()
        return model
    }

    private fun getMessageKey() = databaseReference.child("users").child(user!!.uid).child("messages").push().key
    private fun getAssignmentKey() = databaseReference.child("users").child(user!!.uid).child("assignments").push().key

    fun determineResponseActions(result: Result) {
        when (result.action) {
            Constants.ACTION_ASSIGNMENT_SPECIFIC_CLASS -> {
                Timber.d("Action was specific class")
                val params = result.parameters
                val date = params["date"]?.asString?.trim()
                val assignment = params["assignment-official"]?.asString?.trim()
                val userClass = params["class"]?.asString?.trim()

                if (date.isNullOrEmpty() || assignment.isNullOrEmpty() || userClass.isNullOrEmpty()) {
                    val textResponse = result.fulfillment.speech
                    addMessage(MessageType.RECEIVED, textResponse)
                } else {
                    confirmNewAssignmentSpecificClass(assignment!!, userClass!!, date!!)
                }
            }
            Constants.ACTION_ASSIGNMENT_PROMPTED_CLASS -> {
                Timber.d("Action was prompted class")
                val params = result.parameters
                val date = params["date"]?.asString?.trim()
                val assignment = params["assignment-official"]?.asString?.trim()

                if (date.isNullOrEmpty() || assignment.isNullOrEmpty()) {
                    val textResponse = result.fulfillment.speech
                    addMessage(MessageType.RECEIVED, textResponse)
                } else {
                    confirmNewAssignment(assignment!!, getClassContext(), date!!)
                }
            }
            Constants.ACTION_OVERDUE_ASSIGNMENTS -> {
                getOverdueAssignments(context)
            }
            Constants.ACTION_NEXT_ASSIGNMENT -> {
                getNextAssignment(context)
            }
            Constants.ACTION_REQUEST_HELP -> {
                receiveHelp()
            }
            Constants.ACTION_CURRENT_ASSIGNMENTS -> {
                getCurrentAssignments(context)
            }
            Constants.ACTION_EXAMPLE -> {
                getExamples(context)
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
                    contexts.add(AIContext(getConversationContext()))
                    Timber.d("context is " + getConversationContext())
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
