package com.example.makhovyklab2randomgallery

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SyncService : JobService() {

    private val executorService =
        Executors.newSingleThreadExecutor()

    private var future: Future<*>? = null

    private lateinit var syncState: SyncState

    override fun onStartJob(params: JobParameters): Boolean {
        val app = applicationContext as App

        syncState = app.syncState

        future = executorService.submit {

            var success = false

            try {
                success = app.galleryClient.syncGallery { percentage ->
                    syncState.setProgressPercentage(percentage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gallery synchronization error", e)
            }

            finish(success)

            jobFinished(params, !success)
        }

        syncState.setInProgress(true)

        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        future?.cancel(true)

        finish(false)

        return false
    }

    private fun finish(success: Boolean) {
        syncState.setInProgress(false)
        syncState.setScheduled(false)
        syncState.setHasUpdates(!success)

        if (success) {
            syncState.onSyncFinished()
        } else {
            syncState.onSyncFailed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executorService.shutdownNow()
    }

    companion object {

        private const val TAG = "SyncService"
        private const val JOB_ID = 1

        fun scheduleUpdate(context: Context) {
            val app =
                context.applicationContext as App

            val jobScheduler =
                context.getSystemService(
                    Context.JOB_SCHEDULER_SERVICE
                ) as JobScheduler

            val jobInfo =
                JobInfo.Builder(
                    JOB_ID,
                    ComponentName(
                        context,
                        SyncService::class.java
                    )
                )
                    .setRequiredNetworkType(
                        JobInfo.NETWORK_TYPE_UNMETERED
                    )
                    .build()

            jobScheduler.schedule(jobInfo)

            app.syncState.setScheduled(true)
        }

        fun isScheduled(context: Context): Boolean {
            val jobScheduler =
                context.getSystemService(
                    Context.JOB_SCHEDULER_SERVICE
                ) as JobScheduler

            return jobScheduler.allPendingJobs.any {
                it.id == JOB_ID
            }
        }
    }
}