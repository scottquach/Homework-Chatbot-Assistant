package com.scottquach.homeworkchatbotassistant

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.models.MessageModel
import timber.log.Timber

import java.sql.Timestamp

/**
 * Created by Scott Quach on 9/15/2017.
 *
 * Responsible for sending out custom 'RECEIVE' messages (messages that the user receives)
 */

class MessageHandler(val context: Context) {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private fun saveMessagesToDatabase(messageModels: List<MessageModel>) {
        for (model in messageModels) {
            databaseReference.child("users").child(user!!.uid).child("messages").child(model.key).setValue(model)
        }
    }

    private fun updateConvoContext(convoContext: String, classContext: String) {
        databaseReference.child("users").child(user!!.uid).child("contexts").child("conversation").setValue(convoContext)
        databaseReference.child("users").child(user!!.uid).child("contexts").child("class").setValue(classContext)
    }

    fun receiveWelcomeMessages() {
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

        Timber.d("created welcome message")
        saveMessagesToDatabase(listOf(model1, model2, model3, model4, model5, model6, model7))
    }

    fun receiveHelp() {
        val stringMessages = arrayOf(
                "If you haven't done so please specify your classes in the classes tab",
                "Every time you finish a class, I'll be here to ask you what homework you have whether it be a simple assignment or a big project",
                "Using advanced machine learning, you can answer naturally such as \"I have a chapter 3 summary due next Monday\" or \"I have an exam in 4 days1\"",
                "Or if you don't have any homework you don't have to say anything",
                "You can also add assignments to specific classes later by saying something such as \"Interview Bob for Research Writing by October 5th",
                "Remember that the above statements are just basic examples, feel free to speak the way YOU would naturally speak and I'll learn over time")
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
    }

    fun promptForAssignment(userClass: String): List<MessageModel> {
        val model = MessageModel()
        model.message = "Provide homework for $userClass?"
        model.type = MessageType.RECEIVED.toLong()
        model.key = getMessageKey()
        model.timestamp = Timestamp(System.currentTimeMillis())

        saveMessagesToDatabase(listOf(model))
        updateConvoContext(Constants.CONTEXT_PROMPT_HOMEWORK, userClass)
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

    fun confirmNewAssignment(assignment: String, userClass: String,
                             dueDate: String) {

        val model = createReceivedMessage("Assignment \"$assignment\" for $userClass on $dueDate saved")

        val assignmentKey = getAssignmentKey()
        val assignmentModel = AssignmentModel(assignment, userClass, 0, dueDate, assignmentKey)
        databaseReference.child("users").child(user!!.uid).child("assignments")
                .child(assignmentKey).setValue(assignmentModel)
        val assignmentManager = AssignmentDueManager(context)
        assignmentManager.startNextAlarm(assignmentModel)
        saveMessagesToDatabase(listOf(model))
    }

    /**
     * Creates a message model that specifies the next due assignment and saves it to the database,
     * has a default message if no upcoming assignments
     */
    fun getNextAssignment(context: Context) {
        val assignmentManager = AssignmentTimeManager()
        val nextAssignment = assignmentManager.getNextAssignment(context)

        if (nextAssignment.key == "empty") {
            val messageModel = createReceivedMessage("You don't have any upcoming assignments")
            saveMessagesToDatabase(listOf(messageModel))
        } else {
            val messageModel = createReceivedMessage("Next assignment is \"${nextAssignment.title}\" for ${nextAssignment.userClass}")
            saveMessagesToDatabase(listOf(messageModel))
        }
    }

    /**
     * Creates message models that specify overdue assignments and saves it to the database,
     * has a default message if no overdue assignments
     */
    fun getOverdueAssignments(context: Context) {
        val assignmentManager = AssignmentTimeManager()
        val overdueAssignments = assignmentManager.getOverdueAssignments(context)

        if (overdueAssignments.isEmpty()) {
            val messageModel = createReceivedMessage("You have no overdue assignments!")
            saveMessagesToDatabase(listOf(messageModel))
        } else {
            val messages = mutableListOf<MessageModel>()
            var overdueNumber = 1
            for (assignment in overdueAssignments) {
                val messageModel = createReceivedMessage("$overdueNumber. \"${assignment.title}\"")
                overdueNumber++
                messages.add(messageModel)
            }

            saveMessagesToDatabase(messages)
        }
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
