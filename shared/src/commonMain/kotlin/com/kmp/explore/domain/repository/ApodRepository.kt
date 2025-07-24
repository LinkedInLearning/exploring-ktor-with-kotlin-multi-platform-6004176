package com.kmp.explore.domain.repository

import com.kmp.explore.models.ApodResponse

interface ApodRepository {
    suspend fun fetchApodByDate(date: String): Result<ApodResponse>
}