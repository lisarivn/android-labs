
package com.example.makhovyklab2randomgallery.task

import android.util.Log
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException

class TaskManager {

    companion object {
        private const val TAG = "TaskManager"
    }

    private val executor: ExecutorService =
        Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "GalleryTaskWorker")
        }

    fun <T> execute(
        task: Callable<T>,
        subject: TaskSubject<T>
    ) {

        try {
            executor.execute {
                subject.notifyListeners(
                    Result(status = Status.RUNNING)
                )

                try {
                    Log.d(
                        TAG,
                        "Task started: ${Thread.currentThread().name}"
                    )

                    val data = task.call()

                    subject.notifyListeners(
                        Result(
                            status = Status.SUCCESS,
                            data = data
                        )
                    )

                    Log.d(TAG, "Task completed")

                } catch (e: Exception) {
                    Log.e(TAG, "Task failed", e)

                    subject.notifyListeners(
                        Result(
                            status = Status.ERROR,
                            error = e
                        )
                    )
                }
            }
        } catch (e: RejectedExecutionException) {
            subject.notifyListeners(
                Result(
                    status = Status.ERROR,
                    error = e
                )
            )
        }
    }

    fun shutdown() {
        executor.shutdown()
    }
}
