
package com.example.makhovyklab1var16

import android.app.Application
import com.example.makhovyklab1var16.background.HistoryWorker

class App : Application() {

    lateinit var historyWorker: HistoryWorker
        private set

    override fun onCreate() {
        super.onCreate()

        historyWorker = HistoryWorker(this)
        historyWorker.start()
    }
}
