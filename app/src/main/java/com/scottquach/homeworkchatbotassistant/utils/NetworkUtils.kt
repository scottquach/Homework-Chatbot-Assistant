package com.scottquach.homeworkchatbotassistant.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by Scott Quach on 10/17/2017.
 */
class NetworkUtils {
    companion object {
        fun isConnected(context: Context) : Boolean {
            val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectionManager.activeNetworkInfo
            if (activeNetwork == null) return false
            return activeNetwork.isConnected
        }
    }
}