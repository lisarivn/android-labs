package com.example.makhovyklab2randomgallery

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.os.Handler
import android.os.Looper

class ActionsHandlerService : Service() {

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.d(
            TAG,
            "onStartCommand: action=${intent?.action}"
        )

        when (intent?.action) {

            ACTION_CHECK_FOR_UPDATES -> {
                // ...
            }

            ACTION_CANCEL -> {
                Toast.makeText(
                    this,
                    "Cancelled",
                    Toast.LENGTH_SHORT
                ).show()

                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun checkForUpdates(
        fromNotification: Boolean
    ) {
        val app =
            applicationContext as App

        val client =
            app.galleryClient

        Thread {
            try {
                val hasUpdates =
                    client.hasUpdates()

                app.syncState.setHasUpdates(
                    hasUpdates
                )

                if (
                    fromNotification &&
                    hasUpdates
                ) {
                    showUpdateToast()
                }

            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error checking gallery updates",
                    e
                )
            } finally {
                stopSelf()
            }
        }.start()
    }

    private fun showUpdateToast() {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                this,
                R.string.gallery_update_available,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {

        private const val TAG =
            "ActionsHandlerService"

        const val ACTION_CHECK_FOR_UPDATES =
            "com.example.makhovyklab2randomgallery.CHECK_FOR_UPDATES"

        const val ACTION_CANCEL =
            "com.example.makhovyklab2randomgallery.CANCEL"

        const val EXTRA_FROM_NOTIFICATION =
            "FROM_NOTIFICATION"
    }
}