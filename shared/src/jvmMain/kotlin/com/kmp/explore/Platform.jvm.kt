package com.kmp.explore

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val baseUrl: String = "http://localhost:8080"
}

actual fun getPlatform(): Platform = JVMPlatform()