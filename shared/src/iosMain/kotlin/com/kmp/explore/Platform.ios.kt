package com.kmp.explore

import platform.Foundation.NSProcessInfo
import com.kmp.explore.config.ServerConfig

class IOSPlatform : Platform {
    override val name: String = "iOS"
    override val baseUrl: String = if (isSimulator()) {
        ServerConfig.IOS_SIMULATOR_URL
    } else {
        ServerConfig.DEVICE_URL
    }

    private fun isSimulator(): Boolean {
        val processInfo = NSProcessInfo.processInfo
        return processInfo.environment["SIMULATOR_DEVICE_NAME"] != null
    }
}

actual fun getPlatform(): Platform = IOSPlatform()