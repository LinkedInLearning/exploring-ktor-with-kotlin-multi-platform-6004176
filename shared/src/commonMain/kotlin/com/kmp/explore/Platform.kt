package com.kmp.explore

interface Platform {
    val name: String
    val baseUrl: String
}

expect fun getPlatform(): Platform