package com.scottquach.homeworkchatbotassistant

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scottquach.homeworkchatbotassistant.models.ClassModel
import kotlinx.android.synthetic.main.fragment_create_class.*
import java.sql.Timestamp

class CreateClassFragment : Fragment() {

    private var listener: CreateClassInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        return container?.inflate(R.layout.fragment_create_class)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        floating_confirm.setOnClickListener {
            var newClass = createNewClassModel()
            listener?.let {
                it.addClass(newClass)
                it.switchToDisplayFragment()
            }

        }

        floating_cancel.setOnClickListener {
            listener?.switchToDisplayFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CreateClassInterface) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement ScheduleDisplayListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    fun  createNewClassModel(): ClassModel {
        var newClassModel = ClassModel()
        newClassModel.title = edit_title.text.toString()
        newClassModel.timeStart = Timestamp(edit_time_start.text.toString().toLong())
        newClassModel.timeEnd = Timestamp(edit_time_end.text.toString().toLong())
        newClassModel.days = mutableListOf(1, 2, 3)
        return newClassModel
    }

    interface CreateClassInterface {
        fun addClass(newClass: ClassModel)
        fun switchToDisplayFragment()
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateClassFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): CreateClassFragment {
            val fragment = CreateClassFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
