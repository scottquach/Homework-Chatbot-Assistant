package com.scottquach.homeworkchatbotassistant.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.scottquach.homeworkchatbotassistant.Constants

/**
 * A default AlertDialog subclass
 * that implements the interface AlertDialogInterface
 * to handle interactions
 */
class AlertDialogFragment : DialogFragment() {


    private var listener: AlertDialogInterface? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is AlertDialogInterface) {
            listener= context
        } else {
            throw RuntimeException(context!!.toString() + " must implement AlertDialogInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val title = arguments?.getString(Constants.ALERT_TITLE)
        val message = arguments?.getString(Constants.ALERT_MESSAGE)
        val positiveString = arguments?.getString(Constants.ALERT_POSITIVE)
        val negativeString = arguments?.getString(Constants.ALERT_NEGATIVE)
        val haveNegative = arguments?.getBoolean(Constants.ALERT_HAVE_NEGATIVE)

        val builder =  AlertDialog.Builder(context!!)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveString, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        listener?.onAlertPositiveClicked(dialog)
                    }
                })

        if (haveNegative!!) {
            builder.setNegativeButton(negativeString, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    listener?.onAlertNegativeClicked(dialog)
                }
            })
        }

        return builder.create()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface AlertDialogInterface {
        fun onAlertPositiveClicked(dialog: Dialog)
        fun onAlertNegativeClicked(dialog: Dialog)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * 
         * @return A new instance of fragment AlertDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(title: String = "Alert", message: String = "", positiveString: String = "Yes",
                        negativeString: String = "No", haveNegative: Boolean = true): AlertDialogFragment {

            val fragment = AlertDialogFragment()
            val args = Bundle()
            args.putString(Constants.ALERT_TITLE, title)
            args.putString(Constants.ALERT_MESSAGE, message)
            args.putString(Constants.ALERT_POSITIVE, positiveString)
            args.putString(Constants.ALERT_NEGATIVE, negativeString)
            args.putBoolean(Constants.ALERT_HAVE_NEGATIVE, haveNegative)
            fragment.arguments = args
            return fragment
        }
    }
}
