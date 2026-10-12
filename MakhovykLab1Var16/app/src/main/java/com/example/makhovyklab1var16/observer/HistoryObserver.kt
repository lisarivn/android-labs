package com.example.makhovyklab1var16.observer

import com.example.makhovyklab1var16.model.GameResult

interface HistoryObserver {

    fun onHistoryLoaded(
        results: List<GameResult>
    )
}