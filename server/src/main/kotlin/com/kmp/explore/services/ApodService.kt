package com.kmp.explore.services

import com.kmp.explore.data.dao.ApodDao
import com.kmp.explore.data.dao.CacheMetadataDao
import com.kmp.explore.models.ApodResponse
import com.kmp.explore.models.PaginatedResponse
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import java.time.LocalDate

class ApodService(
    private val nasaApiClient: NasaApiClient,
    private val apodDao: ApodDao,
    private val cacheMetadataDao: CacheMetadataDao,
    private val cacheDays: Int = 90
) {
    private val logger = LoggerFactory.getLogger(ApodService::class.java)

    suspend fun getTodayApod(): ApodResponse {
        val today = LocalDate.now().toString()
        val cachedApod = apodDao.getByDate(today)

        if (cachedApod != null) {
            logger.info("Returning today's APOD from cache")
            return cachedApod
        }

        try {
            logger.info("Fetching today's APOD from NASA API")
            val apod = nasaApiClient.getTodayApod()
            apodDao.save(apod.copy(fetchedAt = System.currentTimeMillis()))
            return apod
        } catch (e: NasaApiException) {
            logger.warn("Rate limit hit when fetching today's APOD. Trying yesterday's as fallback.")
            val yesterday = LocalDate.now().minusDays(1).toString()
            val yesterdayApod = apodDao.getByDate(yesterday)

            if (yesterdayApod != null) {
                return yesterdayApod.copy(
                    title = "[FALLBACK] ${yesterdayApod.title}",
                    explanation = "NASA API rate limit reached. Showing yesterday's image as a fallback.\n\n${yesterdayApod.explanation}"
                )
            }

            throw e
        }
    }

    suspend fun getApodByDate(date: String): ApodResponse {
        validateDate(date)
        val cachedApod = apodDao.getByDate(date)

        return if (cachedApod != null) {
            logger.info("Returning APOD for $date from cache")
            cachedApod
        } else {
            logger.info("Fetching APOD for $date from NASA API")
            val apod = nasaApiClient.getApodByDate(date)
            apodDao.save(apod.copy(fetchedAt = System.currentTimeMillis()))
            apod
        }
    }

    suspend fun getRandomApod(): ApodResponse {
        val randomFromCache = apodDao.getRandom()

        return if (randomFromCache != null) {
            logger.info("Returning random APOD from cache")
            randomFromCache
        } else {
            logger.info("Fetching random APOD from NASA API")
            val apod = nasaApiClient.getRandomApod()
            apodDao.save(apod.copy(fetchedAt = System.currentTimeMillis()))
            apod
        }
    }

    suspend fun getApodHistory(page: Int, pageSize: Int): PaginatedResponse<ApodResponse> {
        require(page > 0) { "Page must be greater than 0" }
        require(pageSize > 0) { "Page size must be greater than 0" }
        require(pageSize <= 100) { "Page size cannot exceed 100" }

        val (items, totalCount) = apodDao.getPaginated(page, pageSize)

        if (items.isEmpty() && totalCount == 0) {
            val today = LocalDate.now()
            val startDate = today.minusDays(minOf(30, cacheDays.toLong()))

            fillHistoricalCache(startDate, today)

            val (newItems, newTotalCount) = apodDao.getPaginated(page, pageSize)
            return PaginatedResponse(
                items = newItems,
                page = page,
                pageSize = pageSize,
                totalItems = newTotalCount,
                totalPages = calculateTotalPages(newTotalCount, pageSize)
            )
        }

        return PaginatedResponse(
            items = items,
            page = page,
            pageSize = pageSize,
            totalItems = totalCount,
            totalPages = calculateTotalPages(totalCount, pageSize)
        )
    }

    suspend fun fillHistoricalCache(startDate: LocalDate, endDate: LocalDate): Int {
        require(!startDate.isAfter(endDate)) { "Start date cannot be after end date" }

        logger.info("Filling historical cache from $startDate to $endDate")

        var currentDate = startDate
        var count = 0

        while (!currentDate.isAfter(endDate)) {
            try {
                if (apodDao.getByDate(currentDate.toString()) == null) {
                    try {
                        val apod = nasaApiClient.getApodByDate(currentDate.toString())
                        apodDao.save(apod.copy(fetchedAt = System.currentTimeMillis()))
                        count++
                        delay(1500)
                    } catch (e: NasaApiException) {
                        if (e.message?.contains("Rate limit") == true) {
                            logger.warn("Rate limit hit while filling cache. Pausing for 5 minutes.")
                            delay(5 * 60 * 1000)
                        } else {
                            throw e
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("fillHistoricalCache Error fetching APOD for $currentDate: ${e.message}")
            }

            currentDate = currentDate.plusDays(1)
        }

        logger.info("Added $count new entries to cache")
        return count
    }

    suspend fun runDailyCacheMaintenanceJob() {
        logger.info("Running daily cache maintenance job")

        try {
            getTodayApod()

            val deletedCount = cleanupOldEntries(cacheDays)
            logger.info("Removed $deletedCount old entries from cache")

            val today = LocalDate.now()
            val oldestDate = today.minusDays(cacheDays.toLong())
            val addedCount = fillMissingEntries(oldestDate, today)
            logger.info("Added $addedCount missing entries to cache")

            cacheMetadataDao.set("daily_maintenance_last_run", System.currentTimeMillis().toString())

            logger.info("Daily cache maintenance completed successfully")
        } catch (e: Exception) {
            logger.error("Error in daily cache maintenance: ${e.message}", e)
        }
    }

    private suspend fun fillMissingEntries(startDate: LocalDate, endDate: LocalDate): Int {
        var currentDate = startDate
        var count = 0

        while (!currentDate.isAfter(endDate)) {
            try {
                if (apodDao.getByDate(currentDate.toString()) == null) {
                    val apod = nasaApiClient.getApodByDate(currentDate.toString())
                    apodDao.save(apod.copy(fetchedAt = System.currentTimeMillis()))
                    count++
                }
            } catch (e: Exception) {
                logger.error("fillMissingEntries Error fetching APOD for $currentDate: ${e.message}")
            }

            currentDate = currentDate.plusDays(1)
        }

        return count
    }

    private suspend fun cleanupOldEntries(keepDays: Int): Int {
        val cutoffDate = LocalDate.now().minusDays(keepDays.toLong())
        return apodDao.deleteOlderThan(cutoffDate)
    }

    suspend fun needsHistoricalDataFetch(): Boolean {
        try {
            val totalCount = apodDao.getTotalCount()

            if (totalCount < 50) {
                logger.info("Database has only $totalCount entries. Historical fetch needed.")
                return true
            }

            val today = LocalDate.now()
            val thirtyDaysAgo = today.minusDays(30)
            val recentCount = apodDao.countInDateRange(thirtyDaysAgo.toString(), today.toString())

            if (recentCount < 25) {
                logger.info("Database has only $recentCount recent entries. Historical fetch needed.")
                return true
            }

            logger.info("Database appears adequately populated ($totalCount total, $recentCount recent)")
            return false

        } catch (e: Exception) {
            logger.error("Error checking database status", e)
            return true
        }
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

    private fun calculateTotalPages(totalItems: Int, pageSize: Int): Int {
        return if (totalItems == 0) 1 else (totalItems + pageSize - 1) / pageSize
    }
}