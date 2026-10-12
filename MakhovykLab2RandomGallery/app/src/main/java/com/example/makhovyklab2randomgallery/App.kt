package com.example.makhovyklab2randomgallery

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.makhovyklab2randomgallery.task.LocalSyncTask
import com.example.makhovyklab2randomgallery.task.TaskListener
import com.example.makhovyklab2randomgallery.task.TaskSubject
import com.example.makhovyklab2randomgallery.task.Status
import com.example.makhovyklab2randomgallery.task.Result
import com.example.makhovyklab2randomgallery.data.FileGalleryStorage
import com.example.makhovyklab2randomgallery.data.GalleryRequestHandler
import com.example.makhovyklab2randomgallery.state.SyncState
import com.example.makhovyklab2randomgallery.task.TaskManager
import com.squareup.picasso.Picasso
import ua.cn.stu.randomgallery.RandomGalleryClient
import java.util.UUID

class App : Application() {

    lateinit var taskManager: TaskManager
        private set

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
        taskManager = TaskManager()

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

        // Створюємо сховище галереї
        galleryStorage = createStorage()

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
        initializeGallery()
    }

    private fun initializeGallery() {
        val subject = TaskSubject<Int>()

        subject.addListener(object : TaskListener<Int> {
            override fun onTaskResult(result: Result<Int>) {
                when (result.status) {
                    Status.PENDING -> {
                        Log.d("GalleryInitialization", "PENDING")
                    }

                    Status.RUNNING -> {
                        syncState.setInProgress(true)
                        Log.d("GalleryInitialization", "RUNNING")
                    }

                    Status.SUCCESS -> {
                        syncState.setInProgress(false)
                        syncState.onSyncFinished()

                        Log.d(
                            "GalleryInitialization",
                            "SUCCESS: copied ${result.data} photos"
                        )
                    }

                    Status.ERROR -> {
                        syncState.setInProgress(false)
                        syncState.onSyncFailed()

                        Log.e(
                            "GalleryInitialization",
                            "ERROR",
                            result.error
                        )
                    }
                }
            }
        })

        val task = LocalSyncTask(
            context = this,
            storage = galleryStorage,
            onProgress = { percentage ->
                syncState.setProgressPercentage(percentage)
            }
        )

        syncState.setProgressPercentage(0)
        syncState.setInProgress(true)

        taskManager.execute(task, subject)
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