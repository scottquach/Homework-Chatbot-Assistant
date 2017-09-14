package com.scottquach.homeworkchatbotassistant

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import timber.log.Timber

/**
 * Created by Scott Quach on 9/13/2017.
 */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false) : View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ViewGroup.test() {
    Timber.d("test for extensions")
}

fun FragmentManager.changeFragment(containerId: Int, fragment: Fragment, addToBackStack: Boolean = true) {
    val transaction = beginTransaction()

    transaction.replace(containerId, fragment)
    if (addToBackStack) transaction.addToBackStack(null)
    transaction.commit()
}