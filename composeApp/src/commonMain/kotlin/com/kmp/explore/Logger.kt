package com.kmp.explore

interface Logger {
    fun d(tag: String, message: String)
}

expect fun getLogger(): Logger