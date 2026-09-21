package com.example.makhovyklab2randomgallery

import android.content.Context
import ua.cn.stu.randomgallery.GalleryStorage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class FileGalleryStorage(
    private val context: Context
) : GalleryStorage {

    private val storageDirectory =
        File(context.filesDir, "gallery")

    private val preferences =
        context.getSharedPreferences(
            "gallery_storage",
            Context.MODE_PRIVATE
        )

    init {
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs()
        }
    }

    override fun read(identifier: String): InputStream {
        val file = getImageFile(identifier)

        return FileInputStream(file)
    }

    override fun write(
        identifier: String,
        name: String
    ): OutputStream {

        preferences.edit()
            .putString(getNameKey(identifier), name)
            .apply()

        val file = getImageFile(identifier)

        return FileOutputStream(file)
    }

    override fun isExists(identifier: String): Boolean {
        return getImageFile(identifier).exists()
    }

    override fun getName(identifier: String): String {
        return preferences.getString(
            getNameKey(identifier),
            ""
        ) ?: ""
    }

    override fun delete(identifier: String) {
        getImageFile(identifier).delete()

        preferences.edit()
            .remove(getNameKey(identifier))
            .apply()
    }

    override fun rename(
        oldIdentifier: String,
        newIdentifier: String
    ) {
        val oldFile = getImageFile(oldIdentifier)
        val newFile = getImageFile(newIdentifier)

        if (oldFile.exists()) {
            oldFile.renameTo(newFile)
        }

        val name = getName(oldIdentifier)

        preferences.edit()
            .remove(getNameKey(oldIdentifier))
            .putString(getNameKey(newIdentifier), name)
            .apply()
    }

    override fun getAll(): List<String> {
        return storageDirectory
            .listFiles()
            ?.filter { it.isFile }
            ?.map { it.name }
            ?: emptyList()
    }

    override fun getTimestamp(): Long {
        return preferences.getLong(
            KEY_TIMESTAMP,
            0L
        )
    }

    override fun setTimestamp(timestamp: Long) {
        preferences.edit()
            .putLong(KEY_TIMESTAMP, timestamp)
            .apply()
    }

    private fun getImageFile(identifier: String): File {
        return File(storageDirectory, identifier)
    }

    private fun getNameKey(identifier: String): String {
        return "name_$identifier"
    }

    fun populateDemoGallery() {

        // Видаляємо старі demo-файли неправильного формату
        getAll()
            .filter { it.startsWith("demo_") }
            .forEach { delete(it) }

        // Якщо коректні stable-фото вже існують,
        // повторно їх не створюємо
        if (getAll().any { it.startsWith("stable_") }) {
            return
        }

        val demoFiles =
            context.assets.list("demo_gallery")
                ?: emptyArray()

        val timestamp =
            System.currentTimeMillis()

        demoFiles.forEachIndexed { index, fileName ->

            val identifier =
                "stable_${timestamp}___demo_${index + 1}"

            val name =
                fileName.substringBeforeLast(".")

            context.assets
                .open("demo_gallery/$fileName")
                .use { input ->

                    write(
                        identifier,
                        name
                    ).use { output ->

                        input.copyTo(output)
                    }
                }
        }

        setTimestamp(timestamp)
    }

    companion object {
        private const val KEY_TIMESTAMP = "gallery_timestamp"
    }
}