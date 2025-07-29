package com.kmp.explore.services

import com.kmp.explore.models.ApodResponse
import com.kmp.explore.models.PaginatedResponse
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ApodService(private val nasaApiClient: NasaApiClient) {
    private val logger = LoggerFactory.getLogger(ApodService::class.java)

    suspend fun getTodayApod(): ApodResponse {
        return try {
            logger.info("Fetching today's APOD")
            nasaApiClient.getTodayApod()
        } catch (e: Exception) {
            logger.error("Failed to fetch today's APOD: ${e.message}")
            throw RuntimeException("Failed to fetch today's APOD: ${e.message}")
        }
    }

    suspend fun getApodByDate(date: String): ApodResponse {
        validateDate(date)
        return try {
            logger.info("Fetching APOD for date $date")
            nasaApiClient.getApodByDate(date)
        } catch (e: Exception) {
            logger.error("Failed to fetch APOD for date $date: ${e.message}")
            throw RuntimeException("Failed to fetch APOD for date $date: ${e.message}")
        }
    }

    suspend fun getRandomApod(): ApodResponse {
        return try {
            logger.info("Fetching random APOD")
            nasaApiClient.getRandomApod()
        } catch (e: Exception) {
            logger.error("Failed to fetch random APOD: ${e.message}")
            throw RuntimeException("Failed to fetch random APOD: ${e.message}")
        }
    }

    suspend fun getApodHistory(page: Int = 1, pageSize: Int = 10): PaginatedResponse<ApodResponse> {
        // For now, return empty pagination - will be implemented with database in later modules
        return PaginatedResponse(
            items = emptyList(),
            page = page,
            pageSize = pageSize,
            totalItems = 0,
            totalPages = 0
        )
    }

    private fun validateDate(date: String) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid date format. Use YYYY-MM-DD format.")
        }
    }
}