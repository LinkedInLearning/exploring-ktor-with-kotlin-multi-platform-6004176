package com.kmp.explore.routes

import com.kmp.explore.models.ErrorResponse
import com.kmp.explore.services.ApodService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Route.apodRoutes() {
    val apodService by inject<ApodService>()
    val logger = LoggerFactory.getLogger("apodRoutes")


    route("/api/apod") {
        get("/today") {
            try {
                val apod = apodService.getTodayApod()
                call.respond(apod)
            } catch (e: Exception) {
                call.respondError(HttpStatusCode.InternalServerError, "Failed to fetch today's APOD: ${e.message}")
            }
        }

        get("/date/{date}") {
            val date = call.parameters["date"]
            if (date == null) {
                call.respondError(HttpStatusCode.BadRequest, "Missing date parameter")
                return@get
            }

            try {
                val apod = apodService.getApodByDate(date)
                call.respond(apod)
            } catch (e: IllegalArgumentException) {
                call.respondError(HttpStatusCode.BadRequest, e.message ?: "Invalid date")
            } catch (e: Exception) {
                call.respondError(
                    HttpStatusCode.InternalServerError,
                    "Failed to fetch APOD for date $date: ${e.message}"
                )
            }
        }

        get("/random") {
            try {
                val apod = apodService.getRandomApod()
                call.respond(apod)
            } catch (e: Exception) {
                call.respondError(HttpStatusCode.InternalServerError, "Failed to fetch random APOD: ${e.message}")
            }
        }

        get("/history") {
            try {
                val page = call.parameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.parameters["pageSize"]?.toIntOrNull() ?: 10

                if (page <= 0 || pageSize <= 0 || pageSize > 100) {
                    call.respondError(
                        HttpStatusCode.BadRequest,
                        "Invalid pagination parameters. Page must be > 0 and pageSize must be between 1 and 100."
                    )
                    return@get
                }

                val history = apodService.getApodHistory(page, pageSize)
                call.respond(history)
            } catch (e: Exception) {
                call.respondError(HttpStatusCode.InternalServerError, "Failed to fetch APOD history: ${e.message}")
            }
        }

    }

    get("/api/admin/db-status") {
        logger.info("In get - /api/admin/db-status")

        try {
            val db = org.jetbrains.exposed.sql.Database.connect(
                url = "jdbc:sqlite:data/randomspace.db",
                driver = "org.sqlite.JDBC"
            )

            val tables = transaction(db) {
                exec("SELECT name FROM sqlite_master WHERE type='table';") { rs ->
                    generateSequence {
                        if (rs.next()) rs.getString(1) else null
                    }.toList()
                } ?: emptyList()
            }

            // Create a flat structure with only string values
            val result = mapOf(
                "status" to "success",
                "message" to "Database connected successfully",
                "dbPath" to "data/randomspace.db",
                "tableCount" to tables.size.toString(),
                "tables" to tables.joinToString(", ")
            )

            call.respond(result)
        } catch (e: Exception) {
            call.respond(mapOf(
                "status" to "error",
                "message" to (e.message ?: "Unknown error"),
                "error" to e.toString()
            ))
        }
    }
}

suspend fun ApplicationCall.respondError(status: HttpStatusCode, message: String) {
    this.respond(status, ErrorResponse(status.value, message))
}