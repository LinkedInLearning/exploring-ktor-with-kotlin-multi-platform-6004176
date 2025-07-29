package com.kmp.explore.network

import com.kmp.explore.models.ApodResponse
import com.kmp.explore.models.PaginatedResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ApodApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    suspend fun getTodaysApod(): ApodResponse {
        return httpClient.get("$baseUrl/api/apod/today").body()
    }

    suspend fun getApodHistory(page: Int, pageSize: Int): PaginatedResponse<ApodResponse> {
        return httpClient.get("$baseUrl/api/apod/history") {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.body()
    }

    suspend fun getApodByDate(date: String): ApodResponse {
        return httpClient.get("$baseUrl/api/apod/date/$date").body()
    }
}