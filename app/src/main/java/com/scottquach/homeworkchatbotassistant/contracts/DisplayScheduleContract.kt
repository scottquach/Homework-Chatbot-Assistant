package com.scottquach.homeworkchatbotassistant.contracts

import android.content.Context
import com.scottquach.homeworkchatbotassistant.models.ClassModel

/**
 * Created by Scott Quach on 10/25/2017.
 */
interface DisplayScheduleContract {

    interface View {
        fun textNoAssignmentSetVisible()
        fun textNoAssignmentSetInvisible()
        fun removeClass(position: Int)
        fun addData(data: List<ClassModel>)
    }

    interface Presenter {
        fun loadData()
        fun deleteClass(context: Context, model: ClassModel, position: Int)
    }
}