package com.kmp.explore.di

import com.kmp.explore.config.appConfigModule
import com.kmp.explore.config.DatabaseConfig
import com.kmp.explore.data.dao.ApodDao
import com.kmp.explore.data.dao.CacheMetadataDao
import com.kmp.explore.services.ApodService
import com.kmp.explore.services.NasaApiClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient(CIO) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
        }
    }

    single { ApodDao() }
    single { CacheMetadataDao() }
    single { NasaApiClient(get()) }
    single { ApodService(get()) }
}