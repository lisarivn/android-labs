package com.example.licenseexplorerlab4.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface LicenseApiService {

    @GET("licenses")
    suspend fun getLicenses(): List<LicenseDto>

    @GET("licenses/{key}")
    suspend fun getLicenseDetails(
        @Path("key") key: String
    ): LicenseDetailDto
}