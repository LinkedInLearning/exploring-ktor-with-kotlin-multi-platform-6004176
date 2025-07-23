package com.kmp.explore

import com.kmp.explore.config.appConfigModule
import com.kmp.explore.config.DatabaseConfig.databaseModule
import com.kmp.explore.config.initializeDatabase
import com.kmp.explore.di.appModule
import com.kmp.explore.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT")?.toIntOrNull() ?: 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appConfigModule, databaseModule, appModule)
    }

    initializeDatabase()
    configureSerialization()
    configureStatusPages()
    configureMonitoring()
    configureRouting()
    configureBackgroundJobs()
}