package com.example.makhovyklab1var16

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper

@Suppress("DEPRECATION")
class HistoryIntentService :
    IntentService("HistoryIntentService") {

    override fun onHandleIntent(intent: Intent?) {

        val storage = ResultStorage(this)

        val results = storage.loadResults()

        Handler(Looper.getMainLooper()).post {

            HistoryObservable.notifyObservers(results)
        }
    }
}