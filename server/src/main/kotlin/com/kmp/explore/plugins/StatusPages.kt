package com.kmp.explore.plugins

import com.kmp.explore.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, cause.message ?: "Invalid request")
            )
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    HttpStatusCode.InternalServerError.value,
                    "An internal error occurred. Please try again later."
                )
            )
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(HttpStatusCode.NotFound.value, "The requested resource was not found")
            )
        }
    }
}