package com.kmp.explore.client

import com.kmp.explore.api.ApodRoutes
import com.kmp.explore.models.ApodResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

//class ApodClient(private val httpClient: HttpClient, private val baseUrl: String = "http://192.0.0.3:8080") {
//
//    suspend fun getTodayApod(): Result<ApodResponse> = get("$baseUrl${ApodRoutes.TODAY}")
//
//    suspend fun getApodByDate(date: String): Result<ApodResponse> = get("$baseUrl${ApodRoutes.BY_DATE}/$date")
//
//    suspend fun getRandomApod(): Result<ApodResponse> = get("$baseUrl${ApodRoutes.RANDOM}")
//
//    suspend fun getApodHistory(page: Int = 1, pageSize: Int = 10): Result<List<ApodResponse>> =
//        get("$baseUrl${ApodRoutes.HISTORY}?page=$page&pageSize=$pageSize")
//
//    private suspend inline fun <reified T> get(url: String): Result<T> {
//        return try {
//            val response: HttpResponse = httpClient.get(url)
//            if (response.status.isSuccess()) {
//                Result.success(response.body())
//            } else {
//                Result.failure(Exception("HTTP ${response.status.value} ${response.status.description}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}