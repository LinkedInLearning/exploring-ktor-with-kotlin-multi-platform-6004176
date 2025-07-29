package com.kmp.explore.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import java.io.File
import com.kmp.explore.data.tables.ApodTable
import com.kmp.explore.data.tables.CacheMetadataTable

object DatabaseConfig {
    val databaseModule = module {
        single {
            val apodConfig = get<ApodConfig>()
            createHikariDataSource("data/randomspace.db")
        }
        single {
            val dataSource = get<HikariDataSource>()
            createDatabase(dataSource)
        }
    }

    private fun createHikariDataSource(dbFilePath: String): HikariDataSource {
        File(dbFilePath).parentFile?.mkdirs()

        val config = HikariConfig().apply {
            driverClassName = "org.sqlite.JDBC"
            jdbcUrl = "jdbc:sqlite:$dbFilePath"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"

            dataSourceProperties["journal_mode"] = "WAL"
            dataSourceProperties["synchronous"] = "NORMAL"

            connectionTestQuery = "SELECT 1"
            validationTimeout = 3000
        }

        return HikariDataSource(config)
    }

    private fun createDatabase(dataSource: HikariDataSource): Database {
        return Database.connect(dataSource)
    }
}

fun Application.initializeDatabase() {
    val database by inject<Database>()

    transaction(database) {
        SchemaUtils.create(ApodTable, CacheMetadataTable)
    }

    log.info("SQLite database initialized successfully at: data/randomspace.db")
}