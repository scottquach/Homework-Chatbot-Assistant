package com.scottquach.homeworkchatbotassistant.models

import java.sql.Timestamp

/**
 * Created by Scott Quach on 9/10/2017.
 */
data class MessageModel(val type:Int, var message:String, var timestamp:Timestamp, var key:String)