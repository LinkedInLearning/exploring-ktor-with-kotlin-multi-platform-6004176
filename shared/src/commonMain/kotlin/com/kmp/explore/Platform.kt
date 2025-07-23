package com.kmp.explore

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform