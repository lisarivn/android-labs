package com.example.licenseexplorerlab4.data.repository

import com.example.licenseexplorerlab4.data.local.LicenseDao
import com.example.licenseexplorerlab4.data.local.LicenseEntity
import com.example.licenseexplorerlab4.data.remote.LicenseApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CancellationException

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
            val remoteLicenses = api.getLicenses()

            val localLicenses = dao.getAllOnce()
                .associateBy { it.key }

            val changedLicenses = remoteLicenses.mapNotNull { remote ->
                val local = localLicenses[remote.key]

                val updated = LicenseEntity(
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

                if (updated != local) updated else null
            }

            if (changedLicenses.isNotEmpty()) {
                dao.insertLicenses(changedLicenses)
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}