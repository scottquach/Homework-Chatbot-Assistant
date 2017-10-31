package com.scottquach.homeworkchatbotassistant.presenters

import com.google.firebase.auth.FirebaseAuth
import com.scottquach.homeworkchatbotassistant.R
import com.scottquach.homeworkchatbotassistant.activities.SettingsActivity
import com.scottquach.homeworkchatbotassistant.contracts.SettingsContract

/**
 * Created by Scott Quach on 10/27/2017.
 */
class SettingsPresenter(val view: SettingsActivity) : SettingsContract.Presenter {
    override fun onBackButton() {
        view.navigateBack()
    }

    override fun signOutUser() {
        FirebaseAuth.getInstance().signOut()
        view.toast(view.getString(R.string.signed_out))
        view.navigateToSignInActivity()
    }

}