package com.kmp.explore

class IosLogger : Logger {
    override fun d(tag: String, message: String) {
        println("[$tag] $message")
    }
}

actual fun getLogger(): Logger = IosLogger()