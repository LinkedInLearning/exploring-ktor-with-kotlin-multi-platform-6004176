package com.kmp.explore

object Constants {
    const val APP_NAME = "APOD Explorer"
    const val APP_VERSION = "1.0.0"

    const val NASA_BASE_URL = "https://api.nasa.gov/planetary/apod"
    const val DEFAULT_PAGE_SIZE = 10
    const val MAX_PAGE_SIZE = 100

    const val APOD_START_DATE = "1995-06-16"

    const val DATE_FORMAT = "yyyy-MM-dd"

    object ErrorMessages {
        const val INVALID_DATE_FORMAT = "Invalid date format. Use YYYY-MM-DD format."
        const val FUTURE_DATE = "Date cannot be in the future."
        const val BEFORE_APOD_START = "No APOD available before 1995-06-16."
        const val INVALID_PAGINATION = "Page must be > 0 and pageSize must be between 1 and 100."
        const val MISSING_DATE_PARAMETER = "Missing date parameter"
        const val INCOMPLETE_DATE_RANGE = "Both startDate and endDate must be provided together or omitted together."
    }

    object CacheKeys {
        const val DAILY_MAINTENANCE_LAST_RUN = "daily_maintenance_last_run"
    }
}