package com.kmp.explore.di

import com.kmp.explore.ui.ApodViewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModel - can access shared module dependencies
    factory { ApodViewModel(get()) }
}