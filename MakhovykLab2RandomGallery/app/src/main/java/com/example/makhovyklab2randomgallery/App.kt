package com.example.makhovyklab2randomgallery

import android.app.Application
import android.content.Context
import com.squareup.picasso.Picasso
import ua.cn.stu.randomgallery.RandomGalleryClient
import java.util.UUID

class App : Application() {

    lateinit var galleryClient: RandomGalleryClient
        private set

    lateinit var syncState: SyncState
        private set

    lateinit var picasso: Picasso
        private set

    lateinit var galleryStorage: FileGalleryStorage
        private set

    override fun onCreate() {
        super.onCreate()

        // Створюємо стан синхронізації
        syncState = SyncState(this)

        // Отримуємо або створюємо ID галереї
        val preferences = getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        var galleryId = preferences.getString(
            KEY_GALLERY_ID,
            null
        )

        if (galleryId == null) {
            galleryId = UUID.randomUUID().toString()

            preferences.edit()
                .putString(KEY_GALLERY_ID, galleryId)
                .apply()
        }

        // Створюємо локальне сховище
        galleryStorage = createStorage()

        // Додаємо демонстраційні фото,
        // якщо сховище ще порожнє
        galleryStorage.populateDemoGallery()

        // Створюємо клієнт галереї
        galleryClient = RandomGalleryClient(
            galleryId,
            galleryStorage
        )

        // Підключаємо локальні фото до Picasso
        val requestHandler = GalleryRequestHandler()

        galleryClient.addListener(requestHandler)

        picasso = Picasso.Builder(this)
            .addRequestHandler(requestHandler)
            .build()
    }

    private fun createStorage(): FileGalleryStorage {
        return FileGalleryStorage(this)
    }

    companion object {
        private const val PREFERENCES_NAME =
            "random_gallery_preferences"

        private const val KEY_GALLERY_ID =
            "gallery_id"
    }
}