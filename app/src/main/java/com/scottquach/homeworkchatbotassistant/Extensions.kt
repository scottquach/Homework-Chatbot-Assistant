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

fun FragmentManager.changeFragment(containerId: Int, fragment: Fragment, addToBackStack: Boolean = true) {
    val transaction = beginTransaction()

    transaction.replace(containerId, fragment)
    if (addToBackStack) transaction.addToBackStack(null)
    transaction.commit()
}

fun FragmentManager.changeFragmentLeftAnimated(containerId: Int, fragment: Fragment, addToBackStack: Boolean = true) {
    val transaction = beginTransaction()
    transaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right, R.anim.enter_right, R.anim.exit_left)
    transaction.replace(containerId, fragment)
    if (addToBackStack) transaction.addToBackStack(null)
    transaction.commit()
}

fun FragmentManager.changeFragmentRightAnimated(containerId: Int, fragment: Fragment, addToBackStack: Boolean = true) {
    val transaction = beginTransaction()
    transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
    transaction.replace(containerId, fragment)
    if (addToBackStack) transaction.addToBackStack(null)
    transaction.commit()
}