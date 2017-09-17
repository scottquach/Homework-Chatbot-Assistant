package com.scottquach.homeworkchatbotassistant

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.scottquach.homeworkchatbotassistant.adapters.RecyclerScheduleAdapter
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import kotlinx.android.synthetic.main.fragment_display_schedule.*
import timber.log.Timber
import java.sql.Timestamp

class DisplayScheduleFragment : Fragment() {

    private var listener: ScheduleDisplayListener? = null

    private lateinit var databaseReference: DatabaseReference
    private var user: FirebaseUser? = null
    private var userClasses = mutableListOf<ClassModel>()

    private val scheduleRecycler by lazy {
        recycler_schedule
    }
    private var scheduleAdapter: RecyclerScheduleAdapter? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ScheduleDisplayListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onStart() {
        super.onStart()
        Timber.d("on start called")
    }

    fun loadData(dataSnapshot: DataSnapshot) {
        userClasses.clear()
        for (ds in dataSnapshot.child("users").child(user?.uid).child("classes").children) {
            var classModel = ClassModel()
            classModel.title = ds.child("title").value as String
            classModel.timeStart = Timestamp(ds.child("timeStart").child("time").value as Long)
            classModel.timeEnd = Timestamp(ds.child("timeEnd").child("time").value as Long)
            var days = mutableListOf<Int>()
            dataSnapshot.child("users").child(user?.uid).child("classes").child(classModel.title).child("day").children.mapTo(days) { (it.value as Long).toInt() }
            classModel.days = days
            userClasses.add(classModel)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        scheduleRecycler.layoutManager = LinearLayoutManager(context)
        scheduleAdapter = RecyclerScheduleAdapter(userClasses)
        scheduleRecycler.adapter = scheduleAdapter
    }

    interface ScheduleDisplayListener {
        fun switchToCreateFragment()
    }
}
