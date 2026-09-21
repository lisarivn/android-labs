package com.example.makhovyklab1var16

interface HistoryObserver {

    fun onHistoryLoaded(
        results: List<GameResult>
    )
}