package com.scottquach.homeworkchatbotassistant

import android.content.Context
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.database.AssignmentDatabaseManager
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.models.MessageModel
import com.scottquach.homeworkchatbotassistant.presenters.ChatPresenter
import timber.log.Timber

import java.sql.Timestamp

/**
 * Created by Scott Quach on 9/15/2017.
 *
 * Responsible for handling database changes involving chat messages as well as loading messages
 * from the database
 */

class MessageHandler(val context: Context, val presenter: ChatPresenter? = null) {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val userMessages = mutableListOf<MessageModel>()


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
                        presenter?.onMessageAdded(model)
                    }, 500)
                } else presenter?.onMessageAdded(model)
            } else presenter?.onMessageAdded(model)
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

    /**
     * Adds a single message from the message string and messageType
     */
    fun addMessage(messageType: Int, message: String) {
        val key = databaseReference.child("users").child(user!!.uid).child("messages").push().key
        val model = MessageModel(messageType.toLong(), message, Timestamp(System.currentTimeMillis()), key)
        saveMessagesToDatabase(listOf(model))
    }

    fun loadMessages() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("Database could not load dataSnapshot")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.child("users").child(user!!.uid).child("messages").children) {
                    val messageModel = MessageModel()
                    messageModel.type = ds.child("type").value as Long
                    messageModel.message = ds.child("message").value as String
                    messageModel.timestamp = Timestamp((ds.child("timestamp").child("time").value as Long))
                    userMessages.add(messageModel)
                }
                presenter?.messagesLoaded()
            }
        })
    }

    /**
     * Retrieve a copy of the list of user messages
     *
     * @return copy
     */
    fun getMessages(): List<MessageModel> {
        val copy = mutableListOf<MessageModel>()
        copy.addAll(userMessages)
        return copy
    }

    /**
     * Create and push the messages shown when the user opens the chat for the
     * first time
     */
    fun receiveWelcomeMessages() {
        val message1 = "Hi there I'm ${context.getString(R.string.assistant_name)}, here to help " +
                "organize and assist you in your busy school life"
        val model1 = createReceivedMessage(message1)

        //Don't yell at the user LOL
        val message2 = "If you haven't PLEASE SET YOUR CLASSES, as the app won't function properly " +
                "without your class information"
        val model2 = createReceivedMessage(message2)

        val message3 = "If you have any questions feel free to ask me for help or even examples! "
        val model3 = createReceivedMessage(message3)

        val message4 = "Otherwise welcome and thank you for giving me a try "
        val model4 = createReceivedMessage(message4)

        Timber.d("created welcome message")
        saveMessagesToDatabase(listOf(model1, model2, model3, model4))
    }

    /**
     * Creates and pushes default messages that provides the user with a short tutorial for help
     */
    fun receiveHelp() {
        val stringMessages = arrayOf(
                "If you haven't done so please specify your classes in the classes tab",
                "Every time you finish a class, I'll be here to ask you what homework you have",
                "Using advanced machine learning, you can answer naturally such as",
                "\"I have a chapter 3 summary due next Monday\"",
                "or",
                "\"I have an exam in 4 days\"",
                "Or if you don't have any homework you don't have to say anything",
                "You can also add assignments to specific classes later by saying something such as \"Interview Bob for Research Writing by October 5th",
                "Remember that the above statements are just basic examples, feel free to speak the way YOU would naturally speak and I'll learn over time",
                "Feel free to ask for examples if you are still confused")
        val messagesModels = stringMessages.map { createReceivedMessage(it) }
        saveMessagesToDatabase(messagesModels)
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
        model.message = "Give me homework for $userClass"
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
    fun confirmNewAssignmentSpecificClass(assignment: String, userClass: String, dueDate: String) {
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

        val model = createReceivedMessage("Assignment \"$assignment\" for $userClass on $dueDate saved")

        val assignmentKey = getAssignmentKey()
        val assignmentModel = AssignmentModel(assignment, userClass.capitalize(), 0, dueDate, assignmentKey)
        databaseReference.child("users").child(user!!.uid).child("assignments")
                .child(assignmentKey).setValue(assignmentModel)
        val assignmentManager = AssignmentDueManager(context)
        assignmentManager.startNextAlarm(assignmentModel)

        saveMessagesToDatabase(listOf(model))
        updateAssignmentContext(assignment)
        updateClassContext(userClass)
    }

    /**
     * Creates a message model that specifies the next due assignment and saves it to the database,
     * has a default message if no upcoming assignments
     */
    fun getNextAssignment(context: Context) {
        val assignmentManager = AssignmentDatabaseManager()
        val nextAssignment = assignmentManager.getNextAssignment(context)

        if (nextAssignment.key == "empty") {
            val messageModel = createReceivedMessage("You don't have any upcoming assignments")
            saveMessagesToDatabase(listOf(messageModel))
        } else {
            val messageModel = createReceivedMessage("Next assignment is \"${nextAssignment.title}\" for ${nextAssignment.userClass} due " +
                    "on ${nextAssignment.dueDate}")
            saveMessagesToDatabase(listOf(messageModel))
            updateAssignmentContext(nextAssignment.title)
            updateClassContext(nextAssignment.userClass)
        }
    }

    /**
     * Creates message models that specify overdue assignments and saves it to the database,
     * has a default message if no overdue assignments
     */
    fun getOverdueAssignments(context: Context) {
        val assignmentManager = AssignmentDatabaseManager()
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
    }

    /**
     * Creates message models that specify what assignments are currently not finished, this
     * includes assignments that overdue (specially labeled). If there are not assignments currently
     * available a default message is displayed
     */
    fun getCurrentAssignments(context: Context) {
        val assignmentManager = AssignmentDatabaseManager()
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
    }

    /**
     * Creates and send message models that provide the user with examples of the apps
     * functionality
     */
    fun getExamples(context: Context) {
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

        val message6 = "Research machine learning for research writing by October 4th"
        val model6 = createSentMessage(message6)

        val message7 = "Assignment \"Research machine learning\" for Research Writing by 2017-10-04 saved"
        val model7 = createReceivedMessage(message7)

        saveMessagesToDatabase(listOf(model1, model2, model3, model4, model5, model6, model7), false)
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
}
