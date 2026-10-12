package com.example.makhovyklab2randomgallery.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.makhovyklab2randomgallery.App
import com.example.makhovyklab2randomgallery.task.LocalSyncTask
import com.example.makhovyklab2randomgallery.task.TaskListener
import com.example.makhovyklab2randomgallery.task.TaskSubject
import com.example.makhovyklab2randomgallery.task.Result
import com.example.makhovyklab2randomgallery.task.Status

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
                startLocalUpdate()
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

    private fun startLocalUpdate() {
        val app = applicationContext as App
        val subject = TaskSubject<Int>()

        subject.addListener(object : TaskListener<Int> {
            override fun onTaskResult(result: Result<Int>) {
                when (result.status) {
                    Status.PENDING -> {
                        Log.d(TAG, "Local update: PENDING")
                    }

                    Status.RUNNING -> {
                        app.syncState.setInProgress(true)
                        Log.d(TAG, "Local update: RUNNING")
                    }

                    Status.SUCCESS -> {
                        app.syncState.setInProgress(false)
                        app.syncState.onSyncFinished()

                        Log.d(
                            TAG,
                            "Local update: SUCCESS, copied ${result.data} photos"
                        )

                        stopSelf()
                    }

                    Status.ERROR -> {
                        app.syncState.setInProgress(false)
                        app.syncState.onSyncFailed()

                        Log.e(TAG, "Local update: ERROR", result.error)

                        stopSelf()
                    }
                }
            }
        })

        val task = LocalSyncTask(
            context = app,
            storage = app.galleryStorage,
            onProgress = { percentage ->
                app.syncState.setProgressPercentage(percentage)
            }
        )

        app.syncState.setProgressPercentage(0)
        app.syncState.setInProgress(true)

        app.taskManager.execute(task, subject)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
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