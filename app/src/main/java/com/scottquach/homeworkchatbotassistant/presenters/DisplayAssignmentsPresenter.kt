package com.scottquach.homeworkchatbotassistant.presenters

import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scottquach.homeworkchatbotassistant.AssignmentDueManager
import com.scottquach.homeworkchatbotassistant.utils.InstrumentationUtils
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager
import com.scottquach.homeworkchatbotassistant.contracts.DisplayAssignmentsContract
import com.scottquach.homeworkchatbotassistant.database.AssignmentDatabase
import com.scottquach.homeworkchatbotassistant.fragments.DisplayAssignmentsFragment
import com.scottquach.homeworkchatbotassistant.logEvent
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel
import com.scottquach.homeworkchatbotassistant.utils.JobSchedulerUtil

/**
 * Created by Scott Quach on 10/25/2017.
 * Presenter for controlling business logic for showing user their current assignments
 */
class DisplayAssignmentsPresenter(val view: DisplayAssignmentsFragment) : DisplayAssignmentsContract.Presenter,
    AssignmentDatabase.AssignmentCallback{

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    private val database by lazy {
        AssignmentDatabase(this)
    }

    private val userAssignments = mutableListOf<AssignmentModel>()

    /**
     * Deletes the assignment from the database and notifies the view of it's deletion. Toggles
     * whether or not there are any assignments labels. Resets data before adding assignments to
     * make sure no repeats are shown. Reschedules all jobs to account for changed assignment list
     */
    override fun deleteAssignment(model: AssignmentModel, position: Int) {
        databaseReference.child("users").child(user!!.uid).child("assignments").child(model.key).removeValue()
        view.removeAssignment(position)
        userAssignments.removeAt(position)

        database.addToNumberOfCompletedAssignments()

        if (userAssignments.size > 0) {
            view.toggleNoHomeworkLabelsInvisible()
        } else view.toggleNoHomeworkLabelsVisible()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobSchedulerUtil.cancelAllJobs(view.context)
            AssignmentDueManager(view.context).requestReschedule()
            NotifyClassEndManager(view.context).startManaging()
        }
        logEvent(InstrumentationUtils.DELETE_ASSIGNMENT)
    }

    /**
     * Calls to the database to start loading data
     */
    override fun requestLoadData() {
        view.resetData()
        database.loadAssignments()
    }

    /**
     * Called by database once data is loaded
     */
    override fun assignmentsCallback(data: List<AssignmentModel>) {
        view.resetData()
        userAssignments.addAll(data)
        view.addData(userAssignments)
        if (userAssignments.size > 0) {
            view.toggleNoHomeworkLabelsInvisible()
        } else view.toggleNoHomeworkLabelsVisible()
    }
}