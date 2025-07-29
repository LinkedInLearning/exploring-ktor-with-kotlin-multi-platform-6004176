package com.kmp.explore.di

import com.kmp.explore.network.ApodApiService
import com.kmp.explore.network.NetworkConfig
import com.kmp.explore.repository.ApodRepository
import com.kmp.explore.repository.ApodRepositoryImpl
import com.kmp.explore.ui.ApodViewModel
import org.koin.dsl.module

val sharedModule = module {

    // Network
    single { NetworkConfig.createHttpClient() }

    // API Service
    single {
        ApodApiService(
            httpClient = get(),
            baseUrl = NetworkConfig.DEFAULT_BASE_URL
        )
    }

    // Repository
    single<ApodRepository> { ApodRepositoryImpl(get()) }

    // ViewModel
    single { ApodViewModel(get()) }
}