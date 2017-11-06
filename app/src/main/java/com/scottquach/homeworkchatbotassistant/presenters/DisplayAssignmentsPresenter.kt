package com.scottquach.homeworkchatbotassistant.presenters

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scottquach.homeworkchatbotassistant.BaseApplication
import com.scottquach.homeworkchatbotassistant.contracts.DisplayAssignmentsContract
import com.scottquach.homeworkchatbotassistant.database.AssignmentDatabaseManager
import com.scottquach.homeworkchatbotassistant.fragments.DisplayAssignmentsFragment
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel

/**
 * Created by Scott Quach on 10/25/2017.
 * Presenter for controlling business logic for showing user their current assignments
 */
class DisplayAssignmentsPresenter(val view: DisplayAssignmentsFragment) : DisplayAssignmentsContract.Presenter {


    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    private val database by lazy {
        AssignmentDatabaseManager(this)
    }

    private val userAssignments = mutableListOf<AssignmentModel>()

    /**
     * Deletes the assignment from the database and notifies the view of it's deletion. Toggles
     * whether or not there are any assignments labels. Resets data before adding assignments to
     * make sure no repeats are shown
     */
    override fun deleteAssignment(model: AssignmentModel, position: Int) {
        databaseReference.child("users").child(user!!.uid).child("assignments").child(model.key).removeValue()
        view.removeAssignment(position)
        userAssignments.removeAt(position)

        database.addToNumberOfCompletedAssignments()

        if (userAssignments.size > 0) {
            view.toggleNoHomeworkLabelsInvisible()
        } else view.toggleNoHomeworkLabelsVisible()
    }

    /**
     * Calls to the database to start loading data
     */
    override fun requestLoadData() {
        database.loadData()
    }

    /**
     * Called by database once the data is loaded
     */
    override fun onDataLoaded(loadedData: List<AssignmentModel>) {
        view.resetData()
        userAssignments.addAll(loadedData)
        view.addData(userAssignments)
        if (userAssignments.size > 0) {
            view.toggleNoHomeworkLabelsInvisible()
        } else view.toggleNoHomeworkLabelsVisible()
    }
}