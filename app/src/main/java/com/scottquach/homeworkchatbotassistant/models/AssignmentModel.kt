package com.scottquach.homeworkchatbotassistant.models

/**
 * Created by Scott Quach on 9/11/2017.
 */
data class AssignmentModel(
        var title: String = "",
        var userClass: String = "",
        var scale: Int = 0,
        var dueDate: String = "",
        var key: String = "empty"
)