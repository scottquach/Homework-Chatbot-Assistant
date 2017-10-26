package com.scottquach.homeworkchatbotassistant.contracts

import com.scottquach.homeworkchatbotassistant.models.AssignmentModel

/**
 * Created by Scott Quach on 10/25/2017.
 */
interface DisplayAssignmentsContract {

    interface View {
        fun textNoHomeworkSetVisible()
        fun textNoHomeworkSetInvisible()
        fun removeAssignment(position: Int)
        fun addData(data: List<AssignmentModel>)
    }

    interface Presenter {
        fun deleteAssignment(model: AssignmentModel, position:Int)
        fun loadData()
    }

}