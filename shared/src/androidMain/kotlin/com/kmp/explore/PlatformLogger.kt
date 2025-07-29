package com.kmp.explore

import android.util.Log

actual class PlatformLogger {
    private val tag = "KMP"

    actual fun logInfo(message: String) {
        Log.i(tag, message)
    }

    actual fun logError(message: String) {
        Log.e(tag, message)
    }
}