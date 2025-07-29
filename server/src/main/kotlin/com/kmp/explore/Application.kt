package com.kmp.explore

import com.kmp.explore.config.appConfigModule
import com.kmp.explore.config.DatabaseConfig
import com.kmp.explore.config.initializeDatabase
import com.kmp.explore.di.*
import com.kmp.explore.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        modules(appConfigModule, DatabaseConfig.databaseModule, appModule)
    }

    initializeDatabase()
    configureSerialization()
    configureCallLogging()
    configureStatusPages()
    configureRouting()
    configureBackgroundJobs()

    log.info("Responding at http://0.0.0.0:8080")
}