package com.scottquach.homeworkchatbotassistant.fragments

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.adapters.RecyclerScheduleAdapter
import com.scottquach.homeworkchatbotassistant.inflate
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import com.scottquach.homeworkchatbotassistant.models.TimeModel
import kotlinx.android.synthetic.main.fragment_display_schedule.*
import timber.log.Timber

class DisplayScheduleFragment : Fragment(), RecyclerScheduleAdapter.ScheduleAdapterInterface {

    private var listener: ScheduleDisplayInterface? = null

    private lateinit var databaseReference: DatabaseReference
    private var user: FirebaseUser? = null
    private var userClasses = mutableListOf<ClassModel>()

    private val scheduleRecycler by lazy {
        recycler_schedule
    }
    private var scheduleAdapter: RecyclerScheduleAdapter? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ScheduleDisplayInterface) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return container?.inflate(R.layout.fragment_display_schedule)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadData(dataSnapshot)
            }

            override fun onCancelled(p0: DatabaseError?) {
                Timber.d(p0?.message)
            }
        })

        floating_create_class.setOnClickListener {
            listener?.switchToCreateFragment()
        }
    }

    fun loadData(dataSnapshot: DataSnapshot) {
        userClasses.clear()
        for (ds in dataSnapshot.child("users").child(user?.uid).child("classes").children) {
            var classModel = ClassModel()
            classModel.title = ds.child("title").value as String
            classModel.timeEnd = TimeModel(ds.child("timeEnd").child("timeEndHour").value as Long,
                    ds.child("timeEnd").child("timeEndMinute").value as Long)
            var days = mutableListOf<Int>()
            dataSnapshot.child("users").child(user!!.uid).child("classes").child(classModel.title).child("day").children.mapTo(days) { (it.value as Long).toInt() }
            classModel.days = days
            userClasses.add(classModel)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = RecyclerScheduleAdapter(userClasses, this@DisplayScheduleFragment)
        scheduleRecycler.apply {
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(context)
        }

        if (text_no_classes?.visibility == View.VISIBLE && !userClasses.isEmpty()) {
            text_no_classes?.visibility = View.INVISIBLE
        }
    }

    interface ScheduleDisplayInterface {
        fun switchToCreateFragment()
    }

    override fun deleteClass(model: ClassModel) {
        databaseReference.child("users").child(user!!.uid).child("classes").child(model.title).removeValue()

        //Delete the assignments for corresponding class
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.child("users").child(user!!.uid).child("assignments").children
                        .filter { it.child("userClass") as String == model.title }
                        .forEach { databaseReference.child("users").child(user!!.uid).child("assignments").child(it.key).removeValue() }
            }
            override fun onCancelled(p0: DatabaseError?) {

            }



        })
    }

}
