package com.example.licenseexplorerlab4.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.licenseexplorerlab4.data.local.LicenseEntity
import com.example.licenseexplorerlab4.data.repository.LicenseRepository
import com.example.licenseexplorerlab4.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LicenseDetailViewModel(
    private val repository: LicenseRepository,
    private val licenseKey: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<LicenseEntity>>(UiState.Loading)

    val uiState: StateFlow<UiState<LicenseEntity>> =
        _uiState.asStateFlow()

    init {
        observeLicense()
        refreshDetails()
    }

    private fun observeLicense() {
        viewModelScope.launch {
            repository.getLicense(licenseKey).collect { license ->
                if (license != null) {
                    val previousState = _uiState.value
                    val isOffline =
                        (previousState as? UiState.Success)?.isOffline ?: false

                    _uiState.value = UiState.Success(
                        data = license,
                        isOffline = isOffline
                    )
                }
            }
        }
    }

    fun refreshDetails() {
        viewModelScope.launch {
            val result = repository.refreshLicenseDetails(licenseKey)

            if (result.isFailure) {
                val currentState = _uiState.value

                if (currentState is UiState.Success) {
                    _uiState.value = currentState.copy(isOffline = true)
                } else {
                    _uiState.value = UiState.Error(
                        "Unable to load license details. Check your internet connection."
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