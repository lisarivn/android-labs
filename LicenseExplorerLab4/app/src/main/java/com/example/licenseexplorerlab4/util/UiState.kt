package com.example.licenseexplorerlab4.util

sealed interface UiState<out T> {

    data object Loading : UiState<Nothing>

    // data is available to reading
    data class Success<T>(
        val data: T,
        val isOffline: Boolean = false // app use local saved data
    ) : UiState<T>

    // data isn't available to reading
    data class Error(
        val message: String
    ) : UiState<Nothing>
}