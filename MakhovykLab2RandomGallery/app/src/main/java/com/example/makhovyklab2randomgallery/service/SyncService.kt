package com.example.makhovyklab2randomgallery.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.makhovyklab2randomgallery.App
import com.example.makhovyklab2randomgallery.task.LocalSyncTask
import com.example.makhovyklab2randomgallery.task.Result
import com.example.makhovyklab2randomgallery.task.Status
import com.example.makhovyklab2randomgallery.task.TaskListener
import com.example.makhovyklab2randomgallery.task.TaskSubject

class SyncService : JobService() {

    private val mainHandler = Handler(Looper.getMainLooper())

    private var activeSubject: TaskSubject<Int>? = null
    private var activeListener: TaskListener<Int>? = null
    private var activeParams: JobParameters? = null

    override fun onStartJob(params: JobParameters): Boolean {
        val app = applicationContext as App

        // Запам'ятовуємо поточне завдання JobScheduler
        activeParams = params

        val subject = TaskSubject<Int>()
        activeSubject = subject

        val listener = object : TaskListener<Int> {
            override fun onTaskResult(result: Result<Int>) {
                if (activeSubject !== subject) return

                when (result.status) {
                    Status.PENDING -> {
                        Log.d(TAG, "PENDING")
                    }

                    Status.RUNNING -> {
                        Log.d(TAG, "RUNNING")
                    }

                    Status.SUCCESS -> {
                        Log.d(TAG, "SUCCESS: copied ${result.data} photos")
                        finishJob(app, params, subject, true)
                    }

                    Status.ERROR -> {
                        Log.e(TAG, "ERROR", result.error)
                        finishJob(app, params, subject, false)
                    }
                }
            }
        }

        activeListener = listener
        subject.addListener(listener)

        app.syncState.setProgressPercentage(0)
        app.syncState.setInProgress(true)

        val task = LocalSyncTask(
            context = app,
            storage = app.galleryStorage,
            onProgress = { percentage ->
                app.syncState.setProgressPercentage(percentage)
            }
        )

        app.taskManager.execute(task, subject)

        return true
    }

    private fun finishJob(
        app: App,
        params: JobParameters,
        subject: TaskSubject<Int>,
        success: Boolean
    ) {
        if (activeSubject !== subject) return

        app.syncState.setInProgress(false)
        app.syncState.setScheduled(false)

        if (success) {
            app.syncState.onSyncFinished()
        } else {
            app.syncState.onSyncFailed()
        }

        activeListener?.let { subject.removeListener(it) }
        activeListener = null
        activeSubject = null
        activeParams = null

        jobFinished(params, false)
    }

    override fun onStopJob(params: JobParameters): Boolean {
        // TaskManager не підтримує скасування окремої задачі.
        // Від'єднуємо слухача, щоб не завершувати зупинений Job повторно.
        val subject = activeSubject

        if (subject != null) {
            activeListener?.let { subject.removeListener(it) }
        }

        activeListener = null
        activeSubject = null
        activeParams = null

        val app = applicationContext as App
        app.syncState.setInProgress(false)
        app.syncState.setScheduled(false)

        // Завдання не переплановуємо автоматично
        return false
    }

    companion object {
        private const val TAG = "SyncService"
        private const val JOB_ID = 1

        fun scheduleUpdate(context: Context) {
            val app = context.applicationContext as App

            val scheduler = context.getSystemService(
                Context.JOB_SCHEDULER_SERVICE
            ) as JobScheduler

            val jobInfo = JobInfo.Builder(
                JOB_ID,
                ComponentName(context, SyncService::class.java)
            )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .build()

            val result = scheduler.schedule(jobInfo)

            app.syncState.setScheduled(
                result == JobScheduler.RESULT_SUCCESS
            )
        }

        fun isScheduled(context: Context): Boolean {
            val scheduler = context.getSystemService(
                Context.JOB_SCHEDULER_SERVICE
            ) as JobScheduler

            return scheduler.allPendingJobs.any {
                it.id == JOB_ID &&
                        it.service.className == SyncService::class.java.name
            }
        }
    }
}
