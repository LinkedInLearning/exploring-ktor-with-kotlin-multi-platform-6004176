package com.kmp.explore

import platform.Foundation.NSLog

actual class PlatformLogger {
    actual fun logInfo(message: String) {
        NSLog("INFO: %@", message)
    }

    actual fun logError(message: String) {
        NSLog("ERROR: %@", message)
    }
}