package com.kmp.explore

import android.util.Log

class AndroidLogger : Logger {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}

actual fun getLogger(): Logger = AndroidLogger()