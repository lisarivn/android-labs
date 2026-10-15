package com.example.licenseexplorerlab4.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.licenseexplorerlab4.data.repository.LicenseRepository

class LicenseListViewModelFactory(
    private val repository: LicenseRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(LicenseListViewModel::class.java)) {
            return LicenseListViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}