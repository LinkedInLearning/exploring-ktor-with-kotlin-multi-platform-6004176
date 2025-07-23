package com.kmp.explore.config

import org.koin.dsl.module

data class AppConfig(
    val nasaApiKey: String = System.getenv("NASA_API_KEY") ?: "DEMO_KEY",
    val cacheDays: Int = System.getenv("CACHE_DAYS")?.toIntOrNull() ?: 90,
    val dbFilePath: String = "data/randomspace.db"
)

val appConfigModule = module {
    single { AppConfig() }
}