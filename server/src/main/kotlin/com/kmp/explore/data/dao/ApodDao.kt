package com.kmp.explore.data.dao

import com.kmp.explore.data.tables.ApodTable
import com.kmp.explore.models.ApodResponse
import com.kmp.explore.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import java.time.LocalDate

class ApodDao {
    suspend fun save(apod: ApodResponse): ApodResponse = dbQuery {
        val date = LocalDate.parse(apod.date)
        val existingEntry = ApodTable.select { ApodTable.date eq date }
            .singleOrNull()

        if (existingEntry != null) {
            ApodTable.update({ ApodTable.date eq date }) {
                it[title] = apod.title
                it[explanation] = apod.explanation
                it[url] = apod.url
                it[hdUrl] = apod.hdUrl
                it[mediaType] = apod.mediaType
                it[copyright] = apod.copyright
                it[thumbnailUrl] = apod.thumbnailUrl
                it[fetchedAt] = apod.fetchedAt
            }
        } else {
            ApodTable.insert {
                it[this.date] = date
                it[title] = apod.title
                it[explanation] = apod.explanation
                it[url] = apod.url
                it[hdUrl] = apod.hdUrl
                it[mediaType] = apod.mediaType
                it[copyright] = apod.copyright
                it[thumbnailUrl] = apod.thumbnailUrl
                it[fetchedAt] = apod.fetchedAt
            }
        }

        apod
    }

    suspend fun getByDate(date: String): ApodResponse? = dbQuery {
        val localDate = LocalDate.parse(date)
        ApodTable.select { ApodTable.date eq localDate }
            .singleOrNull()
            ?.let { resultRow ->
                ApodResponse(
                    date = resultRow[ApodTable.date].toString(),
                    title = resultRow[ApodTable.title],
                    explanation = resultRow[ApodTable.explanation],
                    url = resultRow[ApodTable.url],
                    hdUrl = resultRow[ApodTable.hdUrl],
                    mediaType = resultRow[ApodTable.mediaType],
                    copyright = resultRow[ApodTable.copyright],
                    thumbnailUrl = resultRow[ApodTable.thumbnailUrl],
                    fetchedAt = resultRow[ApodTable.fetchedAt]
                )
            }
    }

    suspend fun getRandom(): ApodResponse? = dbQuery {
        ApodTable.select { ApodTable.mediaType eq "image" }
            .orderBy(Random())
            .limit(1)
            .singleOrNull()
            ?.let { resultRow ->
                ApodResponse(
                    date = resultRow[ApodTable.date].toString(),
                    title = resultRow[ApodTable.title],
                    explanation = resultRow[ApodTable.explanation],
                    url = resultRow[ApodTable.url],
                    hdUrl = resultRow[ApodTable.hdUrl],
                    mediaType = resultRow[ApodTable.mediaType],
                    copyright = resultRow[ApodTable.copyright],
                    thumbnailUrl = resultRow[ApodTable.thumbnailUrl],
                    fetchedAt = resultRow[ApodTable.fetchedAt]
                )
            }
    }

    suspend fun getPaginated(page: Int, pageSize: Int): Pair<List<ApodResponse>, Int> = dbQuery {
        val offset = (page - 1) * pageSize

        val totalCount = ApodTable.selectAll().count()

        val items = ApodTable.selectAll()
            .orderBy(ApodTable.date to SortOrder.DESC)
            .limit(pageSize, offset.toLong())
            .map { resultRow ->
                ApodResponse(
                    date = resultRow[ApodTable.date].toString(),
                    title = resultRow[ApodTable.title],
                    explanation = resultRow[ApodTable.explanation],
                    url = resultRow[ApodTable.url],
                    hdUrl = resultRow[ApodTable.hdUrl],
                    mediaType = resultRow[ApodTable.mediaType],
                    copyright = resultRow[ApodTable.copyright],
                    thumbnailUrl = resultRow[ApodTable.thumbnailUrl],
                    fetchedAt = resultRow[ApodTable.fetchedAt]
                )
            }

        Pair(items, totalCount.toInt())
    }

    suspend fun deleteOlderThan(date: LocalDate): Int = dbQuery {
        ApodTable.deleteWhere { ApodTable.date less date }
    }


    suspend fun getTotalCount(): Int = dbQuery {
        ApodTable.selectAll().count().toInt()
    }

    suspend fun countInDateRange(startDate: String, endDate: String): Int = dbQuery {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)
        ApodTable.select {
            (ApodTable.date greaterEq start) and (ApodTable.date lessEq end)
        }.count().toInt()
    }
}