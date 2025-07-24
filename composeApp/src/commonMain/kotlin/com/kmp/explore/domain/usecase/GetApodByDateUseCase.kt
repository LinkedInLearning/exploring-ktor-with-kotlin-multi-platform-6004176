package com.kmp.explore.domain.usecase
import com.kmp.explore.models.ApodResponse
import com.kmp.explore.domain.repository.ApodRepository
interface ApodRepository {
    suspend fun fetchApodByDate(date: String): Result<ApodResponse>
}

class GetApodByDateUseCase(
    private val repository: ApodRepository
) {
    suspend operator fun invoke(date: String): Result<ApodResponse> {
        return repository.fetchApodByDate(date)
    }
}