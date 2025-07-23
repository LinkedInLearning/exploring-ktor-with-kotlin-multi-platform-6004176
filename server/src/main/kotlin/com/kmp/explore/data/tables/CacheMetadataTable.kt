package com.kmp.explore.data.tables

import org.jetbrains.exposed.sql.Table

object CacheMetadataTable : Table("cache_metadata") {
    val key = varchar("key", 100)
    val value = varchar("value", 255)
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(key)
}