package com.scottquach.homeworkchatbotassistant

import android.content.Context
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import timber.log.Timber
import java.util.*

/**
 * Created by Scott Quach on 10/10/2017.
 *
 * Responsible for containing helper methods that involve retrieving assignments based on time,
 * such as retrieving overdue assignments or the next upcoming assignment
 */
class AssignmentTimeManager {

    private var userAssignments: MutableList<AssignmentModel> = BaseApplication.getInstance().database
            .getAssignments().toMutableList()

    /**
     * Returns the model of the next assignment that is due, does not include overdue assignments.
     *  If there are no upcoming assignments, return an empty model
     *
     * @param context
     * @return iteratedAssignment
     */
    fun getNextAssignment(context: Context): AssignmentModel {
        val futureAssignments = userAssignments.filter {
            StringUtils.convertStringToCalendar(context, it.dueDate).after(Calendar.getInstance())
        }


        if (futureAssignments.isEmpty()) {
            return AssignmentModel()
        }

        Timber.d("Future assignments was " + futureAssignments)


        var iteratedTime = StringUtils.convertStringToCalendar(context, futureAssignments[0].dueDate)
        var iteratedAssignment = futureAssignments[0]
        for (model in futureAssignments) {
            if (StringUtils.convertStringToCalendar(context, model.dueDate).before(iteratedTime)) {
                iteratedTime = StringUtils.convertStringToCalendar(context, model.dueDate)
                iteratedAssignment = model
            }
        }

        Timber.d("Next Assignment is " + iteratedAssignment)
        return iteratedAssignment
        TODO("Add more refined sourcing, currently only includes day")
    }

    /**
     * Returns an immutable list of AssignmentModels that had a due date that occurs before the
     * current time. If no overdue assignments, returns an empty list
     * @param context
     */
    fun getOverdueAssignments(context: Context): List<AssignmentModel> {
        val currentTime = Calendar.getInstance()

        val overdueAssignments = mutableListOf<AssignmentModel>()
        userAssignments.filterTo(overdueAssignments) {
            StringUtils.convertStringToCalendar(context, it.dueDate).before(currentTime)
        }

        return overdueAssignments.toList()
    }

    /**
     * Returns true if the parameter AssignmentModel has a due date that occurs before the current
     * time, false if it doesn't
     *
     * @param context, model
     */
    fun isOverdueAssignment(context: Context, model: AssignmentModel): Boolean {
        val currentTime = Calendar.getInstance()
        return StringUtils.convertStringToCalendar(context, model.dueDate).before(currentTime)
    }
}