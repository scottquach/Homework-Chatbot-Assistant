package com.scottquach.homeworkchatbotassistant.models

import java.sql.Timestamp

/**
 * Created by Scott Quach on 9/11/2017.
 */
data class ClassModel(var title:String, var timeStart:Timestamp, var timeEnd:Timestamp, var days:List<Int>)