package com.example.licenseexplorerlab4.data.repository

import com.example.licenseexplorerlab4.data.local.LicenseDao
import com.example.licenseexplorerlab4.data.local.LicenseEntity
import com.example.licenseexplorerlab4.data.remote.LicenseApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class LicenseRepository(
    private val api: LicenseApiService,
    private val dao: LicenseDao
) {
    private val gson = Gson()

    // return list of licenses from Room via Flow
    fun getLicenses(): Flow<List<LicenseEntity>> {
        return dao.getAllLicenses()
    }

    // return concrete license from Room
    fun getLicense(key: String): Flow<LicenseEntity?> {
        return dao.getLicenseByKey(key)
    }

    // load list from the API and update the cache
    suspend fun refreshLicenses(): Result<Unit> {
        return try {
            val remoteLicenses = api.getLicenses() // execute an HTTP-req

            // read records that have already been saved
            val localLicenses = dao.getAllOnce()
                // convert list into a dict, where the key is the license ID
                .associateBy { it.key }

            val updatedLicenses = remoteLicenses.map { remote ->
                val local = localLicenses[remote.key]

                LicenseEntity(
                    key = remote.key,
                    name = remote.name,
                    spdxId = remote.spdxId,
                    url = remote.url,
                    description = local?.description,
                    body = local?.body,
                    permissions = local?.permissions,
                    conditions = local?.conditions,
                    limitations = local?.limitations
                )
            }

            dao.insertLicenses(updatedLicenses)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // load details of license and save it
    suspend fun refreshLicenseDetails(key: String): Result<Unit> {
        return try {
            val remote = api.getLicenseDetails(key)

            val entity = LicenseEntity(
                key = remote.key,
                name = remote.name,
                spdxId = remote.spdxId,
                url = "https://api.github.com/licenses/${remote.key}",
                description = remote.description,
                body = remote.body,
                permissions = gson.toJson(remote.permissions),
                conditions = gson.toJson(remote.conditions),
                limitations = gson.toJson(remote.limitations)
            )

            dao.insertLicense(entity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}