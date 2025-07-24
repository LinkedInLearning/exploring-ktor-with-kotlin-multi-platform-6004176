package com.kmp.explore.api

object ApodRoutes {
    private const val BASE = "/api/apod"
    const val TODAY = "$BASE/today"
    const val BY_DATE = "$BASE/date"
    const val RANDOM = "$BASE/random"
    const val HISTORY = "$BASE/history"
    const val DB_STATUS = "/api/admin/db-status"
}