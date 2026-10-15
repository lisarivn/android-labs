package com.example.licenseexplorerlab4

import android.app.Application
import androidx.room.Room
import com.example.licenseexplorerlab4.data.local.LicenseDatabase
import com.example.licenseexplorerlab4.data.remote.RetrofitClient
import com.example.licenseexplorerlab4.data.repository.LicenseRepository

class LicenseApplication : Application() {

    val database: LicenseDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            LicenseDatabase::class.java,
            "licenses_database"
        ).build()
    }

    val repository: LicenseRepository by lazy {
        LicenseRepository(
            api = RetrofitClient.apiService,
            dao = database.licenseDao()
        )
    }
}