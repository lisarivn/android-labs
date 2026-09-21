package com.example.makhovyklab2randomgallery

import android.os.Handler
import android.os.Looper
import android.content.Context

class SyncState(context: Context) {

    private var scheduled =
        SyncService.isScheduled(context)
    private var inProgress = false
    private var hasUpdates = false
    private var progressPercentage = 0

    private val listeners =
        mutableSetOf<Listener>()

    private val handler =
        Handler(Looper.getMainLooper())

    fun addListener(listener: Listener) {
        listeners.add(listener)
        listener.onSyncStateChanged(this)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun isScheduled(): Boolean {
        return scheduled
    }

    fun isInProgress(): Boolean {
        return inProgress
    }

    fun hasUpdates(): Boolean {
        return hasUpdates
    }

    fun getProgressPercentage(): Int {
        return progressPercentage
    }

    fun setScheduled(value: Boolean) {
        scheduled = value
        notifyListeners()
    }

    fun setInProgress(value: Boolean) {
        inProgress = value
        notifyListeners()
    }

    fun setHasUpdates(value: Boolean) {
        hasUpdates = value
        notifyListeners()
    }

    fun setProgressPercentage(value: Int) {
        progressPercentage = value
        notifyListeners()
    }

    private fun notifyListeners() {
        handler.post {
            listeners.forEach { listener ->
                listener.onSyncStateChanged(this)
            }
        }
    }

    fun onSyncFinished() {
        handler.post {
            listeners.forEach { listener ->
                listener.onSyncFinished()
            }
        }
    }

    fun onSyncFailed() {
        handler.post {
            listeners.forEach { listener ->
                listener.onSyncFailed()
            }
        }
    }

    interface Listener {

        fun onSyncStateChanged(
            syncState: SyncState
        )

        fun onSyncFinished()

        fun onSyncFailed()
    }
}