package com.scottquach.homeworkchatbotassistant

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scottquach.homeworkchatbotassistant.adapters.RecyclerScheduleAdapter
import kotlinx.android.synthetic.main.fragment_display_schedule.*

class DisplayScheduleFragment : Fragment() {

    private var listener: ScheduleDisplayListener? = null
    private val recyclerSchedule by lazy {
        recycler_schedule
    }
    private var adapter : RecyclerScheduleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        recyclerSchedule.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return container?.inflate(R.layout.fragment_display_schedule)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        floating_create_class.setOnClickListener {
            listener?.switchToCreateFragment()
        }
    }

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

    interface ScheduleDisplayListener {
        fun switchToCreateFragment()
    }
}
