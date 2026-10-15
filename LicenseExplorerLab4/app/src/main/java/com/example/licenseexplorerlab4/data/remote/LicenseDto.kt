package com.example.licenseexplorerlab4.data.remote

import com.google.gson.annotations.SerializedName

data class LicenseDto(
    val key: String,
    val name: String,

    @SerializedName("spdx_id")
    val spdxId: String?,

    val url: String?
)