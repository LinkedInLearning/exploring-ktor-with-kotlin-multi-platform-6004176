package com.kmp.explore.services

import com.kmp.explore.models.ApodResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

class NasaApiClient(
    private val httpClient: HttpClient,
    private val apiKey: String = "DEMO_KEY"
) {
    private val logger = LoggerFactory.getLogger(NasaApiClient::class.java)
    private val baseUrl = "https://api.nasa.gov/planetary/apod"

    suspend fun getTodayApod(): ApodResponse {
        logger.info("Fetching today's APOD from NASA API")
        delay(100) // Basic rate limiting
        return httpClient.get("$baseUrl?api_key=$apiKey").body()
    }

    suspend fun getApodByDate(date: String): ApodResponse {
        logger.info("Fetching APOD for date $date from NASA API")
        delay(100) // Basic rate limiting
        return httpClient.get("$baseUrl?api_key=$apiKey&date=$date").body()
    }

    suspend fun getRandomApod(): ApodResponse {
        logger.info("Fetching random APOD from NASA API")
        delay(100) // Basic rate limiting
        val response: List<ApodResponse> = httpClient.get("$baseUrl?api_key=$apiKey&count=1").body()
        return response.first()
    }
}

class NasaApiException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)