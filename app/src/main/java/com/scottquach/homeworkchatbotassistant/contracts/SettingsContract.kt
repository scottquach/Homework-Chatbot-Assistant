package com.scottquach.homeworkchatbotassistant.contracts

/**
 * Created by Scott Quach on 10/27/2017.
 */
interface SettingsContract {
    interface View {
        fun navigateToSignInActivity()
        fun navigateBack()
        fun toast(message: String)

    }

    interface Presenter {
        fun signOutUser()
        fun resyncAlarms()
        fun onBackButton()
    }
}