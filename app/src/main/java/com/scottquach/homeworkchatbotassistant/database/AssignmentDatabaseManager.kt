package com.scottquach.homeworkchatbotassistant.database

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.BaseApplication
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.presenters.DisplayAssignmentsPresenter
import com.scottquach.homeworkchatbotassistant.utils.StringUtils
import timber.log.Timber
import java.util.*

/**
 * Created by Scott Quach on 10/10/2017.
 *
 * Responsible for containing helper methods that involve retrieving assignments based on time,
 * such as retrieving overdue assignments or the next upcoming assignment
 */
class AssignmentDatabaseManager(caller: Any) : BaseDatabase() {

    private var userAssignments: MutableList<AssignmentModel> = BaseApplication.getInstance().database
            .getAssignments().toMutableList()

    private var listener: AssignmentCallback? = null

    interface AssignmentCallback {
        fun assignmentsCallback(data: List<AssignmentModel>)
    }

    init {
        if (caller is AssignmentCallback) {
            listener = caller as AssignmentCallback
        }
    }

    /**
     * Loads data for DisplayAssignmentsPresenter
     */
    fun loadAssignments() {
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("Error loading data")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userAssignments.removeAll(userAssignments)
                for (ds in dataSnapshot.child("users").child(user!!.uid).child("assignments").children) {
                    val model = AssignmentModel()
                    model.title = ds.child("title").value as String
                    model.dueDate = ds.child("dueDate").value as String
                    model.userClass = ds.child("userClass").value as String
                    model.scale = (ds.child("scale").value as Long).toInt()
                    model.key = ds.child("key").value as String

                    userAssignments.add(model)
                }
                listener?.assignmentsCallback(userAssignments.toList())
            }
        })
    }

    /**
     * Returns the model of the next assignment that is due.
     *  If there are no upcoming assignments, return an empty model
     *
     * @param context
     * @return iteratedAssignment
     */
    fun getNextAssignment(context: Context): AssignmentModel {
//        val futureAssignments = userAssignments.filter {
//            StringUtils.convertStringToCalendar(context, it.dueDate).after(Calendar.getInstance())
//        }

        val futureAssignments = userAssignments


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
     * Returns a copy of all user assignments as a list
     * @return copy
     */
    fun getCurrentAssignments(context: Context): List<AssignmentModel> {
        val copy = mutableListOf<AssignmentModel>()
        copy.addAll(userAssignments)
        return copy
    }

    /**
     * Number of assignments completed are kept track for future user profile summaries, this
     * increments the number of completed assignments and is called when an assignment is deleted.
     */
    fun addToNumberOfCompletedAssignments() {
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("Could not load data")
            }

            override fun onDataChange(p0: DataSnapshot) {
                var totalCount = p0.child("users").child(user!!.uid).child("profile").child("completed_assignments").value

                if (totalCount != null) {
                    if ((totalCount as Long) > 0) {
                        databaseReference.child("users").child(user!!.uid).child("profile").child("completed_assignments").setValue(++totalCount)
                    }
                } else {
                    databaseReference.child("users").child(user!!.uid).child("profile").child("completed_assignments").setValue(1)
                }
            }
        })
    }

    companion object {
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
}