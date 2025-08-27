package com.kmp.explore.di

import com.kmp.explore.config.ApodConfig
import com.kmp.explore.data.dao.ApodDao
import com.kmp.explore.data.dao.CacheMetadataDao
import com.kmp.explore.services.ApodService
import com.kmp.explore.services.NasaApiClient
import org.koin.dsl.module

val appModule = module {
    single { ApodConfig.load() }

    single {
        val config = get<ApodConfig>()
        NasaApiClient(config.nasaApiKey)
    }

    single { ApodDao() }
    single { CacheMetadataDao() }

    single {
        val config = get<ApodConfig>()
        ApodService(
            nasaApiClient = get(),
            apodDao = get(),
            cacheMetadataDao = get(),
            cacheDays = config.cacheDays
        )
    }
}