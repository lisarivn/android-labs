package com.example.makhovyklab2randomgallery.task

import android.os.Handler
import android.os.Looper

class TaskSubject<T> {

    private val listeners = mutableSetOf<TaskListener<T>>()
    private val mainHandler = Handler(Looper.getMainLooper())

    private var currentResult: Result<T> =
        Result(status = Status.PENDING)

    fun addListener(listener: TaskListener<T>) {
        mainHandler.post {
            listeners.add(listener)
            listener.onTaskResult(currentResult)
        }
    }

    fun removeListener(listener: TaskListener<T>) {
        mainHandler.post {
            listeners.remove(listener)
        }
    }

    fun notifyListeners(result: Result<T>) {
        mainHandler.post {
            currentResult = result

            listeners.toList().forEach { listener ->
                listener.onTaskResult(result)
            }
        }
    }
}