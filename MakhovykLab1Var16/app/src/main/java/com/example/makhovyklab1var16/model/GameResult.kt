package com.example.makhovyklab1var16.model

data class GameResult(
    val dateTime: String,
    val score: Int,
    val gridSize: Int,
    val gameTime: Int,
    val speedOption: Int
)