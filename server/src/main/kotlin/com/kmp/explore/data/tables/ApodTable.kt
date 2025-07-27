package com.kmp.explore.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object ApodTable : Table("apod") {
    val date = date("date").uniqueIndex()
    val title = varchar("title", 255)
    val explanation = text("explanation")
    val url = varchar("url", 500)
    val hdUrl = varchar("hd_url", 500).nullable()
    val mediaType = varchar("media_type", 20)
    val copyright = varchar("copyright", 255).nullable()
    val thumbnailUrl = varchar("thumbnail_url", 500).nullable()
    val fetchedAt = long("fetched_at")

    override val primaryKey = PrimaryKey(date)
}