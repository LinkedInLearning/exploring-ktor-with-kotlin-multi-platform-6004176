package com.kmp.explore.plugins

import com.kmp.explore.config.ApodConfig
import com.kmp.explore.services.ApodService
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration.Companion.minutes

fun Application.configureBackgroundJobs() {
    val logger = LoggerFactory.getLogger("BackgroundJobs")
    val apodService by inject<ApodService>()
    val config by inject<ApodConfig>()
    val scope = CoroutineScope(Dispatchers.IO)

    config.logConfiguration()

    scope.launch {
        try {
            logger.info("Starting APOD initialization sequence...")

            logger.info("Fetching today's APOD")
            apodService.getTodayApod()

            startHistoricalDataFetching(apodService, logger, config)

        } catch (e: Exception) {
            logger.error("Error during startup initialization", e)
        }
    }

    scope.launch {
        while (true) {
            try {
                val now = LocalDateTime.now()
                val targetTime = LocalTime.of(config.maintenanceHour, 0)

                val nextRun = if (now.toLocalTime().isAfter(targetTime)) {
                    now.toLocalDate().plusDays(1).atTime(targetTime)
                } else {
                    now.toLocalDate().atTime(targetTime)
                }

                val waitTime = Duration.between(now, nextRun)
                logger.info("Next cache maintenance job scheduled for ${nextRun.toLocalDate()} at ${nextRun.toLocalTime()} (in ${waitTime.toHours()} hours)")

                delay(waitTime.toMillis())

                logger.info("Running scheduled cache maintenance job")
                apodService.runDailyCacheMaintenanceJob()

            } catch (e: Exception) {
                logger.error("Error scheduling or running cache maintenance job", e)
                delay(60.minutes.inWholeMilliseconds)
            }

            delay(Duration.ofHours(22).toMillis())
        }
    }

    logger.info("Background jobs configured successfully")
}

private fun CoroutineScope.startHistoricalDataFetching(
    apodService: ApodService,
    logger: org.slf4j.Logger,
    config: ApodConfig
) = launch {
    try {
        val today = LocalDate.now()
        val firstApodDate = LocalDate.of(1995, 6, 16)

        val needsInitialFetch = apodService.needsHistoricalDataFetch()

        if (needsInitialFetch) {
            logger.info("Database appears empty or incomplete. Starting historical data fetch...")

            val initialBatchSize = config.getEffectiveInitialBatchSize()
            val followupBatchSize = config.getEffectiveFollowupBatchSize()

            logger.info("Using batch sizes - Initial: $initialBatchSize, Followup: $followupBatchSize")

            val initialStartDate = today.minusDays(initialBatchSize.toLong())
            logger.info("Fetching initial batch: $initialBatchSize pictures from $initialStartDate to $today")

            val initialCount = apodService.fillHistoricalCache(initialStartDate, today)
            logger.info("Initial fetch completed: $initialCount new entries added")

            startFollowUpBatches(apodService, logger, initialStartDate, firstApodDate, followupBatchSize, config)
        } else {
            logger.info("Historical data appears complete. Skipping bulk fetch.")
            val recentStartDate = today.minusDays(7)
            apodService.fillHistoricalCache(recentStartDate, today)
        }

    } catch (e: Exception) {
        logger.error("Error in historical data fetching", e)
    }
}

private fun CoroutineScope.startFollowUpBatches(
    apodService: ApodService,
    logger: org.slf4j.Logger,
    currentStartDate: LocalDate,
    firstApodDate: LocalDate,
    batchSize: Int,
    config: ApodConfig
) = launch {
    var workingStartDate = currentStartDate
    val intervalMinutes = config.getEffectiveBatchIntervalMinutes()

    logger.info("Follow-up batches will run every $intervalMinutes minutes${if (config.debugMode) " (DEBUG MODE)" else ""}")

    while (workingStartDate.isAfter(firstApodDate)) {
        try {
            logger.info("Waiting $intervalMinutes minutes before next batch...")
            delay(intervalMinutes * 60 * 1000)

            val batchEndDate = workingStartDate.minusDays(1)
            val batchStartDate = maxOf(
                batchEndDate.minusDays(batchSize.toLong()),
                firstApodDate
            )

            logger.info("Fetching follow-up batch: $batchSize pictures from $batchStartDate to $batchEndDate")

            val count = apodService.fillHistoricalCache(batchStartDate, batchEndDate)
            logger.info("Follow-up batch completed: $count new entries added")

            workingStartDate = batchStartDate

            if (workingStartDate <= firstApodDate) {
                logger.info("Historical data fetch completed! All pictures from $firstApodDate to present have been processed.")
                break
            }

        } catch (e: Exception) {
            logger.error("Error in follow-up batch fetch", e)
        }
    }
}