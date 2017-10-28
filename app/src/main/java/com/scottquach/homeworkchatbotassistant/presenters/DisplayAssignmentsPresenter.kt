package com.scottquach.homeworkchatbotassistant.presenters

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scottquach.homeworkchatbotassistant.BaseApplication
import com.scottquach.homeworkchatbotassistant.contracts.DisplayAssignmentsContract
import com.scottquach.homeworkchatbotassistant.fragments.DisplayAssignmentsFragment
import com.scottquach.homeworkchatbotassistant.models.AssignmentModel

/**
 * Created by Scott Quach on 10/25/2017.
 * Presenter for controlling business logic for showing user their current assignments
 */
class DisplayAssignmentsPresenter(val view: DisplayAssignmentsFragment) : DisplayAssignmentsContract.Presenter {

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    private lateinit var userAssignments: MutableList<AssignmentModel>

    /**
     * Deletes the assignment from the database and notifies the view of it's deletion. Toggles
     * whether or not there are any assignments labels
     */
    override fun deleteAssignment(model: AssignmentModel, position: Int) {
        databaseReference.child("users").child(user!!.uid).child("assignments").child(model.key).removeValue()
        view.removeAssignment(position)
        userAssignments.removeAt(position)

        if (userAssignments.size > 0) {
            view.toggleNoHomeworkLabelsInvisible()
        } else view.toggleNoHomeworkLabelsVisible()
    }

    /**
     * Loads data from the database and pushes it to the view to be displayed. Toggles the no homework labels
     * depending on data retrieved
     */
    override fun loadData() {
        userAssignments = BaseApplication.getInstance().database.getAssignments().toMutableList()
        view.addData(userAssignments)
        if (userAssignments.size > 0) {
            view.toggleNoHomeworkLabelsInvisible()
        } else view.toggleNoHomeworkLabelsVisible()
    }
}