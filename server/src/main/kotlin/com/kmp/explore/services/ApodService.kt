package com.kmp.explore.services

import com.kmp.explore.models.ApodResponse
import com.kmp.explore.models.PaginatedResponse
import org.slf4j.LoggerFactory
import java.time.LocalDate

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

    suspend fun getApodHistory(page: Int, pageSize: Int): PaginatedResponse<ApodResponse> {
        require(page > 0) { "Page must be greater than 0" }
        require(pageSize > 0) { "Page size must be greater than 0" }
        require(pageSize <= 100) { "Page size cannot exceed 100" }

        return PaginatedResponse(
            items = emptyList(),
            page = page,
            pageSize = pageSize,
            totalItems = 0,
            totalPages = 0
        )
    }

    private fun validateDate(dateStr: String): LocalDate {
        val parsedDate = try {
            LocalDate.parse(dateStr)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid date format. Use YYYY-MM-DD format.")
        }

        val today = LocalDate.now()
        if (parsedDate.isAfter(today)) {
            throw IllegalArgumentException("Date cannot be in the future.")
        }

        val apodStartDate = LocalDate.of(1995, 6, 16)
        if (parsedDate.isBefore(apodStartDate)) {
            throw IllegalArgumentException("No APOD available before 1995-06-16.")
        }

        return parsedDate
    }
}