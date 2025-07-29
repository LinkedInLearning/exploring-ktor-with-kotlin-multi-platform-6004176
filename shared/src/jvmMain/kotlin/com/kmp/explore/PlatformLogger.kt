package com.kmp.explore

actual class PlatformLogger {
    actual fun logInfo(message: String) {
        println("INFO: $message")
    }

    actual fun logError(message: String) {
        System.err.println("ERROR: $message")
    }
}