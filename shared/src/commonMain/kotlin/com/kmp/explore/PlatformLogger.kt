package com.kmp.explore

expect class PlatformLogger {
    fun logInfo(message: String)
    fun logError(message: String)
}