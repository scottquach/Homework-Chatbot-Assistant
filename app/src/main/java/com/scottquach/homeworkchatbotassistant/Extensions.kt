package com.scottquach.homeworkchatbotassistant

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Scott Quach on 9/13/2017.
 */

fun Any.logEvent(event: String) {
    BaseApplication.getInstance().instrumentation.logEvent(event)
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun FragmentManager.changeFragment(containerId: Int, fragment: Fragment, addToBackStack: Boolean = true
                                   , canGoBack: Boolean = true) {

    popBackStack("fragment_tag", FragmentManager.POP_BACK_STACK_INCLUSIVE);

    val transaction = beginTransaction()

    transaction.replace(containerId, fragment, fragment.javaClass.name)
    if (canGoBack) {
        if (addToBackStack) {
            transaction.addToBackStack(null)
        } else {
            transaction.addToBackStack("fragment_tag")
        }
    }
    transaction.commit()
}

fun FragmentManager.changeFragmentLeftAnimated(containerId: Int, fragment: Fragment, addToBackStack: Boolean = true
                                               , canGoBack: Boolean = true) {
    popBackStack("fragment_tag", FragmentManager.POP_BACK_STACK_INCLUSIVE);

    val transaction = beginTransaction()
    transaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right, R.anim.enter_right, R.anim.exit_left)
    transaction.replace(containerId, fragment, fragment.javaClass.name)

    if (canGoBack) {
        if (addToBackStack) {
            transaction.addToBackStack(null)
        } else {
            transaction.addToBackStack("fragment_tag")
        }
    }
    transaction.commit()
}

fun FragmentManager.changeFragmentRightAnimated(containerId: Int, fragment: Fragment, addToBackStack: Boolean = true
                                                , canGoBack: Boolean = true) {
    popBackStack("fragment_tag", FragmentManager.POP_BACK_STACK_INCLUSIVE);

    val transaction = beginTransaction()
    transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
    transaction.replace(containerId, fragment, fragment.javaClass.name)

    if (canGoBack) {
        if (addToBackStack) {
            transaction.addToBackStack(null)
        } else {
            transaction.addToBackStack("fragment_tag")
        }
    }
    transaction.commit()
}