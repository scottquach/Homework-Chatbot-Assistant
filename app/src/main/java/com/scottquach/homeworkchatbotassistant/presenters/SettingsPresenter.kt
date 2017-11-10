package com.scottquach.homeworkchatbotassistant.presenters

import com.google.firebase.auth.FirebaseAuth
import com.scottquach.homeworkchatbotassistant.*
import com.scottquach.homeworkchatbotassistant.activities.SettingsActivity
import com.scottquach.homeworkchatbotassistant.contracts.SettingsContract
import com.scottquach.homeworkchatbotassistant.utils.InstrumentationUtils
import com.scottquach.homeworkchatbotassistant.utils.JobSchedulerUtil

/**
 * Created by Scott Quach on 10/27/2017.
 */
class SettingsPresenter(val view: SettingsActivity) : SettingsContract.Presenter {

    override fun resyncAlarms() {
        JobSchedulerUtil.cancelAllJobs(view)
        AssignmentDueManager(view).requestReschedule()
        NotifyClassEndManager(view).startManaging(System.currentTimeMillis())
    }

    override fun onBackButton() {
        view.navigateBack()
    }

    override fun signOutUser() {
        logEvent(InstrumentationUtils.SIGN_OUT)
        FirebaseAuth.getInstance().signOut()
        view.toast(view.getString(R.string.signed_out))
        view.navigateToSignInActivity()
    }

}