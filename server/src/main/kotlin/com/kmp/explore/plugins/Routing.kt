package com.kmp.explore.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            val javaVersion = System.getProperty("java.version")
            call.respondText("Ktor: Hello, Java $javaVersion!")
        }
        get("/health") {
            call.respondText("Server is running")
        }
    }
}