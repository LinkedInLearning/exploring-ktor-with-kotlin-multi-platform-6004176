package com.kmp.explore.config

import org.jetbrains.exposed.sql.Database
//import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initializeDatabase() {
    Database.connect(
        url = "jdbc:sqlite:data/randomspace.db",
        driver = "org.sqlite.JDBC"
    )

    // Create tables will be added in later modules
    transaction {
        // SchemaUtils.create() calls will be added when we create tables
    }
}