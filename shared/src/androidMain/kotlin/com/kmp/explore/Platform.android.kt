package com.kmp.explore

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android"
    override val baseUrl: String = if (isEmulator()) {
        "http://10.0.2.2:8080"  // Emulator
    } else {
        "http://100.64.228.194:8080"  // Updated IP
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
                "google_sdk" == Build.PRODUCT ||
                Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu"))
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()