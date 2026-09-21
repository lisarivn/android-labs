package com.example.makhovyklab1var16

object HistoryObservable {

    private val observers =
        mutableSetOf<HistoryObserver>()

    fun addObserver(
        observer: HistoryObserver
    ) {
        observers.add(observer)
    }

    fun removeObserver(
        observer: HistoryObserver
    ) {
        observers.remove(observer)
    }

    fun notifyObservers(
        results: List<GameResult>
    ) {
        observers.forEach { observer ->
            observer.onHistoryLoaded(results)
        }
    }
}