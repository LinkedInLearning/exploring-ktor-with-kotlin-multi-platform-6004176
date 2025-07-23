package com.kmp.explore.data.dao

import com.kmp.explore.data.tables.CacheMetadataTable
import com.kmp.explore.util.dbQuery
import org.jetbrains.exposed.sql.*

class CacheMetadataDao {
    suspend fun set(key: String, value: String): Unit = dbQuery {
        val now = System.currentTimeMillis()

        val updated = CacheMetadataTable.update({ CacheMetadataTable.key eq key }) {
            it[CacheMetadataTable.value] = value
            it[updatedAt] = now
        }

        if (updated == 0) {
            CacheMetadataTable.insert {
                it[CacheMetadataTable.key] = key
                it[CacheMetadataTable.value] = value
                it[updatedAt] = now
            }
        }
    }

    suspend fun get(key: String): String? = dbQuery {
        CacheMetadataTable.select { CacheMetadataTable.key eq key }
            .singleOrNull()
            ?.get(CacheMetadataTable.value)
    }

    suspend fun getLastUpdated(key: String): Long? = dbQuery {
        CacheMetadataTable.select { CacheMetadataTable.key eq key }
            .singleOrNull()
            ?.get(CacheMetadataTable.updatedAt)
    }
}