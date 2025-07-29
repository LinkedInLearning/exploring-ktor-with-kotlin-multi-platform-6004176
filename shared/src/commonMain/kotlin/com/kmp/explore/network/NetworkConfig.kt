package com.kmp.explore.network

import com.kmp.explore.getPlatform
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkConfig {
    val DEFAULT_BASE_URL = getPlatform().baseUrl

    fun createHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
}