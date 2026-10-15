package com.example.licenseexplorerlab4.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LicenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LicenseDatabase : RoomDatabase() {

    abstract fun licenseDao(): LicenseDao
}