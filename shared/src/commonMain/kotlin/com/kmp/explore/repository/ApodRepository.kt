package com.kmp.explore.repository

import com.kmp.explore.models.ApodResponse
import com.kmp.explore.models.PaginatedResponse
import com.kmp.explore.network.ApodApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ApodRepository {
    suspend fun getTodayApod(): Result<ApodResponse>
    suspend fun getApodHistory(page: Int, pageSize: Int): Result<PaginatedResponse<ApodResponse>>
    suspend fun getApodByDate(date: String): Result<ApodResponse>
    fun getTodayApodFlow(): Flow<Result<ApodResponse>>
}

class ApodRepositoryImpl(
    private val apiService: ApodApiService
) : ApodRepository {

    override suspend fun getTodayApod(): Result<ApodResponse> {
        return try {
            val response = apiService.getTodaysApod()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getApodHistory(page: Int, pageSize: Int): Result<PaginatedResponse<ApodResponse>> {
        return try {
            val response = apiService.getApodHistory(page, pageSize)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getApodByDate(date: String): Result<ApodResponse> {
        return try {
            val response = apiService.getApodByDate(date)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTodayApodFlow(): Flow<Result<ApodResponse>> = flow {
        emit(getTodayApod())
    }
}