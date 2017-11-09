package com.scottquach.homeworkchatbotassistant

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.database.BaseDatabase
import com.scottquach.homeworkchatbotassistant.models.MessageModel
import timber.log.Timber
import java.sql.Timestamp


/**
 * Created by Scott Quach on 11/6/2017.
 */
class ChatDatabaseManager(val context: Context, val caller: Any) : BaseDatabase() {
    val userMessages = mutableListOf<MessageModel>()

    private var listener: CallbackInterface? = null

    interface CallbackInterface {
        fun chatDatabaseCallback(data: List<MessageModel>)
    }

    init {
        if (caller is CallbackInterface) {
            listener = caller as CallbackInterface
        }
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
                listener?.chatDatabaseCallback(userMessages)
            }
        })
    }
}