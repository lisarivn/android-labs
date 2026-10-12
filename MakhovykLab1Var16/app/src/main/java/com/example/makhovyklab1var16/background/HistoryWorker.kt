
package com.example.makhovyklab1var16.background

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.makhovyklab1var16.data.ResultStorage
import com.example.makhovyklab1var16.model.GameResult
import com.example.makhovyklab1var16.observer.HistoryObservable

class HistoryWorker(
    context: Context
) : Thread("HistoryWorker") {

    companion object {
        private const val TAG = "HistoryWorker"
    }

    private val storage = ResultStorage(context.applicationContext)

    private val mainHandler = Handler(Looper.getMainLooper())

    private val ready = java.util.concurrent.CountDownLatch(1)

    private lateinit var workerHandler: Handler

    override fun run() {
        Looper.prepare()

        workerHandler = Handler(Looper.myLooper()!!)

        Log.d(TAG, "Worker started: ${Thread.currentThread().name}")

        ready.countDown()

        Looper.loop()

        Log.d(TAG, "Worker stopped")
    }

    fun saveResult(result: GameResult) {
        enqueue {
            try {
                storage.saveResult(result)
                Log.d(TAG, "Result saved in ${Thread.currentThread().name}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save result", e)
            }
        }
    }

    fun loadResults() {
        enqueue {
            try {
                val results = storage.loadResults()

                Log.d(TAG, "History loaded: ${results.size} results")

                mainHandler.post {
                    HistoryObservable.notifyObservers(results)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load history", e)
            }
        }
    }

    private fun enqueue(action: () -> Unit) {
        if (ready.count == 0L) {
            check(workerHandler.post(action)) {
                "HistoryWorker is not accepting tasks"
            }
        } else {
            mainHandler.post {
                enqueue(action)
            }
        }
    }

    fun shutdown() {
        if (ready.count == 0L) {
            workerHandler.looper.quitSafely()
        }
    }
}
