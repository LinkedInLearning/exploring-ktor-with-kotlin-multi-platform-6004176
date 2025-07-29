package com.kmp.explore.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApodResponse(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String? = null,
    @SerialName("hdurl") val hdUrl: String? = null,
    @SerialName("media_type") val mediaType: String,
    val copyright: String? = null,
    val thumbnailUrl: String? = null,
    val fetchedAt: Long = 0
)

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String
)