package com.example.licenseexplorerlab4.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.licenseexplorerlab4.data.repository.LicenseRepository

class LicenseDetailViewModelFactory(
    private val repository: LicenseRepository,
    private val licenseKey: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(LicenseDetailViewModel::class.java)) {
            return LicenseDetailViewModel(repository, licenseKey) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}