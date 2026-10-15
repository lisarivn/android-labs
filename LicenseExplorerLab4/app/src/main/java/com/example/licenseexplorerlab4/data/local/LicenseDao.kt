package com.example.licenseexplorerlab4.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LicenseDao {

    // Flow for automatically update the interface when
    // changes was made in DB
    @Query("SELECT * FROM licenses ORDER BY name ASC")
    fun getAllLicenses(): Flow<List<LicenseEntity>>

    @Query("SELECT * FROM licenses WHERE `key` = :key LIMIT 1")
    fun getLicenseByKey(key: String): Flow<LicenseEntity?>

    // suspend for one-time asynchronically operations
    @Query("SELECT * FROM licenses")
    suspend fun getAllOnce(): List<LicenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLicenses(licenses: List<LicenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLicense(license: LicenseEntity)
}