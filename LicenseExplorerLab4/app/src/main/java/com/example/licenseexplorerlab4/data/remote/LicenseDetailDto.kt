package com.example.licenseexplorerlab4.data.remote

import com.google.gson.annotations.SerializedName

data class LicenseDetailDto(
    val key: String,
    val name: String,

    @SerializedName("spdx_id")
    val spdxId: String?,

    val description: String?,
    val body: String?,
    val permissions: List<String>?,
    val conditions: List<String>?,
    val limitations: List<String>?
)