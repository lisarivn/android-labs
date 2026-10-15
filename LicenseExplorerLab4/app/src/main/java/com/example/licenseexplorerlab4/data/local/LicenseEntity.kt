package com.example.licenseexplorerlab4.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// table in SQLite
@Entity(tableName = "licenses")
data class LicenseEntity(
    @PrimaryKey
    val key: String,

    val name: String,
    val spdxId: String?,
    val url: String?,

    val description: String? = null,
    val body: String? = null,

    val permissions: String? = null,
    val conditions: String? = null,
    val limitations: String? = null
)