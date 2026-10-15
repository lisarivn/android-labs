package com.example.licenseexplorerlab4.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.licenseexplorerlab4.data.local.LicenseEntity
import com.example.licenseexplorerlab4.data.repository.LicenseRepository
import com.example.licenseexplorerlab4.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LicenseListViewModel(
    private val repository: LicenseRepository
) : ViewModel() {

    // it will only change on the inside ViewModel
    private val _uiState =
        MutableStateFlow<UiState<List<LicenseEntity>>>(UiState.Loading)

    // read-only activity
    val uiState: StateFlow<UiState<List<LicenseEntity>>> =
        _uiState.asStateFlow()

    // after creating the ViewModel, we start observing the local DB
    // and try to update it with API
    init {
        observeLicenses()
        refreshLicenses()
    }

    private fun observeLicenses() {
        // launches the coroutine connected to lifecycle ViewModel
        viewModelScope.launch {
            // subscribes to the Room stream, when the data in the DB changes,
            // Flow will send the updated list, and the ViewModel will update the screen's state
            repository.getLicenses().collectLatest { licenses ->
                val currentState = _uiState.value

                val isOffline =
                    (currentState as? UiState.Success)?.isOffline ?: false

                if (licenses.isNotEmpty()) {
                    _uiState.value = UiState.Success(
                        data = licenses,
                        isOffline = isOffline
                    )
                }
            }
        }
    }

    fun refreshLicenses() {
        viewModelScope.launch {
            val result = repository.refreshLicenses()

            if (result.isFailure) {
                val currentState = _uiState.value

                if (currentState is UiState.Success) {
                    _uiState.value = currentState.copy(isOffline = true)
                } else {
                    _uiState.value = UiState.Error(
                        "Не вдалося завантажити ліцензії. Перевірте підключення до інтернету."
                    )
                }
            } else {
                val currentState = _uiState.value

                if (currentState is UiState.Success) {
                    _uiState.value = currentState.copy(isOffline = false)
                }
            }
        }
    }
}