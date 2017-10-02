package com.scottquach.homeworkchatbotassistant.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.activities.SettingsActivity
import com.scottquach.homeworkchatbotassistant.inflate
import kotlinx.android.synthetic.main.fragment_navigation.*

class NavigationFragment : Fragment() {

    private var listener: NavigationFragmentInterface? = null

    interface NavigationFragmentInterface {
        fun startClassScheduleActivity()
        fun startDisplayHomeworkActivity()
        fun startMainActivity()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
            return container?.inflate(R.layout.fragment_navigation)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_settings.setOnClickListener {
            startActivity(Intent(context, SettingsActivity::class.java))
        }

        card_navigation_classes.setOnClickListener {
            listener?.startClassScheduleActivity()
        }

        card_navigation_homework.setOnClickListener {
            listener?.startDisplayHomeworkActivity()
        }

        card_navigation_chat.setOnClickListener {
            listener?.startMainActivity()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is NavigationFragmentInterface) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement DisplayHomeworkInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }



}
