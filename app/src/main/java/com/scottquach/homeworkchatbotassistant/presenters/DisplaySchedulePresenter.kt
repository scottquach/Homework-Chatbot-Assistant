package com.scottquach.homeworkchatbotassistant.presenters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scottquach.homeworkchatbotassistant.BaseApplication
import com.scottquach.homeworkchatbotassistant.NotifyClassEndManager
import com.scottquach.homeworkchatbotassistant.contracts.DisplayScheduleContract
import com.scottquach.homeworkchatbotassistant.fragments.DisplayScheduleFragment
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import timber.log.Timber

/**
 * Created by Scott Quach on 10/25/2017.
 * Presenter that controls the business logic for displaying the users classes
 */
class DisplaySchedulePresenter(val view: DisplayScheduleFragment) : DisplayScheduleContract.Presenter {

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser

    private lateinit var userClasses: MutableList<ClassModel>

    override fun loadData() {
        userClasses = BaseApplication.getInstance().database.getClasses().toMutableList()
        view.addData(userClasses)

        if (userClasses.size > 0){
            view.textNoAssignmentSetInvisible()
        } else view.textNoAssignmentSetVisible()
    }

    override fun deleteClass(context: Context, model: ClassModel, position: Int) {

        AlertDialog.Builder(context)
                .setTitle("Are you sure?")
                .setPositiveButton("Delete", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        databaseReference.child("users").child(user!!.uid).child("classes").child(model.title).removeValue()
                        //Delete the assignments for corresponding class
                        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                dataSnapshot.child("users").child(user!!.uid).child("assignments").children
                                        .filter { it.child("userClass").value as String == model.title }
                                        .forEach { databaseReference.child("users").child(user!!.uid).child("assignments").child(it.key).removeValue() }
                                val manager = NotifyClassEndManager(context)
                                manager.startManaging()
                            }

                            override fun onCancelled(p0: DatabaseError?) {
                                Timber.e("Error loading data " + p0.toString())
                            }
                        })
                        view.removeClass(position)
                        userClasses.removeAt(position)
                    }
                })
                .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                })
                .create().show()

        if (userClasses.size > 0){
            view.textNoAssignmentSetInvisible()
        } else view.textNoAssignmentSetVisible()
    }

}