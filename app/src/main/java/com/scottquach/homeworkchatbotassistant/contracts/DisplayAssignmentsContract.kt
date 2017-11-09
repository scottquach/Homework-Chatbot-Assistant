package com.scottquach.homeworkchatbotassistant.contracts

import com.scottquach.homeworkchatbotassistant.models.AssignmentModel

/**
 * Created by Scott Quach on 10/25/2017.
 */
interface DisplayAssignmentsContract {

    interface View {
        fun toggleNoHomeworkLabelsVisible()
        fun toggleNoHomeworkLabelsInvisible()
        fun removeAssignment(position: Int)
        fun resetData()
        fun addData(data: List<AssignmentModel>)
        fun notifyNoInternet()
    }

    interface Presenter {
        fun deleteAssignment(model: AssignmentModel, position:Int)
        fun requestLoadData()
    }

}