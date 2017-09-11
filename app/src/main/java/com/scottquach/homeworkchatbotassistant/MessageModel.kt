package com.scottquach.homeworkchatbotassistant

import java.sql.Timestamp

/**
 * Created by Scott Quach on 9/10/2017.
 */
data class MessageModel(val type:Int, var message:String, var timestamp: Timestamp)