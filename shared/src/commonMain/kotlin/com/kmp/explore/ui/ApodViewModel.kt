package com.kmp.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.explore.models.ApodResponse
import com.kmp.explore.repository.ApodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val apodList: List<ApodResponse> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
)

data class TodayUiState(
    val isLoading: Boolean = false,
    val todayApod: ApodResponse? = null,
    val error: String? = null
)

data class FindUiState(
    val isLoading: Boolean = false,
    val foundApod: ApodResponse? = null,
    val error: String? = null,
    val searchDate: String = ""
)

class ApodViewModel(
    private val repository: ApodRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _todayUiState = MutableStateFlow(TodayUiState())
    val todayUiState: StateFlow<TodayUiState> = _todayUiState.asStateFlow()

    private val _findUiState = MutableStateFlow(FindUiState())
    val findUiState: StateFlow<FindUiState> = _findUiState.asStateFlow()

    private val pageSize = 10

    init {
        loadApodHistory()
        loadTodayApod() // Also load today's APOD on init
    }

    fun loadApodHistory() {
        if (_homeUiState.value.isLoading) return

        viewModelScope.launch {
            _homeUiState.value = _homeUiState.value.copy(isLoading = true, error = null)

            repository.getApodHistory(_homeUiState.value.currentPage, pageSize)
                .onSuccess { paginatedResponse ->
                    val currentList = _homeUiState.value.apodList
                    val newList = if (_homeUiState.value.currentPage == 1) {
                        paginatedResponse.items
                    } else {
                        currentList + paginatedResponse.items
                    }

                    _homeUiState.value = _homeUiState.value.copy(
                        isLoading = false,
                        apodList = newList,
                        hasMorePages = paginatedResponse.page < paginatedResponse.totalPages,
                        currentPage = _homeUiState.value.currentPage + 1
                    )
                }
                .onFailure { error ->
                    _homeUiState.value = _homeUiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun refresh() {
        _homeUiState.value = HomeUiState()
        loadApodHistory()
    }

    fun loadMoreItems() {
        if (_homeUiState.value.hasMorePages && !_homeUiState.value.isLoading) {
            loadApodHistory()
        }
    }

    fun loadTodayApod() {
        viewModelScope.launch {
            _todayUiState.value = _todayUiState.value.copy(isLoading = true, error = null)

            repository.getTodayApod() // Use the correct method name from your repository
                .onSuccess { apod ->
                    _todayUiState.value = _todayUiState.value.copy(
                        isLoading = false,
                        todayApod = apod,
                        error = null
                    )
                }
                .onFailure { error ->
                    _todayUiState.value = _todayUiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load today's picture"
                    )
                }
        }
    }

    fun refreshTodayApod() {
        loadTodayApod()
    }

    fun updateSearchDate(date: String) {
        _findUiState.value = _findUiState.value.copy(
            searchDate = date,
            foundApod = null, // Clear previous result when date changes
            error = null
        )
    }

    fun searchApodByDate(date: String) {
        // Basic date validation
        if (!isValidDateFormat(date)) {
            _findUiState.value = _findUiState.value.copy(
                error = "Please enter a valid date in YYYY-MM-DD format"
            )
            return
        }

        viewModelScope.launch {
            _findUiState.value = _findUiState.value.copy(isLoading = true, error = null)

            repository.getApodByDate(date)
                .onSuccess { apod ->
                    _findUiState.value = _findUiState.value.copy(
                        isLoading = false,
                        foundApod = apod,
                        error = null
                    )
                }
                .onFailure { error ->
                    _findUiState.value = _findUiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to find picture for that date"
                    )
                }
        }
    }

    fun clearFindResults() {
        _findUiState.value = FindUiState()
    }

    private fun isValidDateFormat(date: String): Boolean {
        return try {
            // Basic regex check for YYYY-MM-DD format
            val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
            if (!regex.matches(date)) return false

            // Additional validation can be added here
            // For example, checking if the date is not in the future
            // and not before June 16, 1995 (first APOD date)

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getTodayApod(): ApodResponse {
        return repository.getTodayApod().getOrThrow() // Use correct method name
    }

    suspend fun getApodByDate(date: String): ApodResponse {
        return repository.getApodByDate(date).getOrThrow()
    }
}