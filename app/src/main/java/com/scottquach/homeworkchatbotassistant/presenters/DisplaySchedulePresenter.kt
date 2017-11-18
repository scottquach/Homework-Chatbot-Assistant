package com.scottquach.homeworkchatbotassistant.presenters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.AssignmentDueManager
import com.scottquach.homeworkchatbotassistant.utils.InstrumentationUtils
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.contracts.DisplayScheduleContract
import com.scottquach.homeworkchatbotassistant.database.ClassDatabase
import com.scottquach.homeworkchatbotassistant.fragments.DisplayScheduleFragment
import com.scottquach.homeworkchatbotassistant.logEvent
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.utils.JobSchedulerUtil
import timber.log.Timber

/**
 * Created by Scott Quach on 10/25/2017.
 * Presenter that controls the business logic for displaying the users classes
 */
class DisplaySchedulePresenter(val view: DisplayScheduleFragment) : DisplayScheduleContract.Presenter {

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    private val userClasses = mutableListOf<ClassModel>()

    private val database by lazy {
        ClassDatabase(this)
    }

    /**
     * Retrieves data from Database and pushes it to the view to be displayed. Resets data
     * before updating to make sure no repeats are shown
     */
    override fun requestLoadData() {
        view.resetData()
        view.setTextLabel(view.context.getString(R.string.loading_classes))
        view.textLabelSetVisible()
        database.loadData()
    }

    /**
     * Called by database when data is loaded
     */
    override fun onDataLoaded(loadedData: List<ClassModel>) {
        userClasses.addAll(loadedData)
        view.addData(userClasses)

        if (userClasses.size > 0) {
            view.textLabelSetInvisible()
        } else {
            view.setTextLabel(view.context.getString(R.string.no_classes))
            view.textLabelSetVisible()
        }
    }

    /**
     * Shows an alert dialog to confirm deletion before deleting the item. Notifies the View
     * of the items deletion
     */
    override fun deleteClass(context: Context, model: ClassModel, position: Int) {

        AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.are_you_sure))
                .setPositiveButton(context.getString(R.string.delete), object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        databaseReference.child("users").child(user!!.uid).child("classes").child(model.title).removeValue()
                        //Delete the assignments for corresponding class
                        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                JobSchedulerUtil.cancelAllJobs(view.context)
                                dataSnapshot.child("users").child(user!!.uid).child("assignments").children
                                        .filter { it.child("userClass").value as String == model.title }
                                        .forEach { databaseReference.child("users").child(user!!.uid).child("assignments").child(it.key).removeValue() }
                                val manager = NotifyClassEndManager(context)
                                manager.startManaging()
                                AssignmentDueManager(view.context).requestReschedule()
                            }

                            override fun onCancelled(p0: DatabaseError?) {
                                Timber.e("Error loading data " + p0.toString())
                            }
                        })
                        view.removeClass(position)
                        userClasses.removeAt(position)

                        if (userClasses.size > 0) {
                            view.textLabelSetInvisible()
                        } else {
                            view.setTextLabel(view.context.getString(R.string.no_classes))
                            view.textLabelSetVisible()
                        }
                        logEvent(InstrumentationUtils.DELETE_CLASS)
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                })
                .create().show()
    }
}