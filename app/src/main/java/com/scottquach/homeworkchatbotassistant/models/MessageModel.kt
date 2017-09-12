package com.scottquach.homeworkchatbotassistant.models

import java.sql.Timestamp

/**
 * Created by Scott Quach on 9/10/2017.
 */
data class MessageModel(
        var type:Long = -1,
        var message:String = "",
        var timestamp:Timestamp? = null,
        var key:String = "")