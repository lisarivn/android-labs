package com.example.makhovyklab1var16

import android.content.Context

class ResultStorage(
    private val context: Context
) {

    companion object {
        private const val FILE_NAME = "game_results.txt"
    }

    fun saveResult(result: GameResult) {

        val line = buildString {
            append(result.dateTime)
            append("|")
            append(result.score)
            append("|")
            append(result.gridSize)
            append("|")
            append(result.gameTime)
            append("|")
            append(result.speedOption)
            append("\n")
        }

        context.openFileOutput(
            FILE_NAME,
            Context.MODE_APPEND
        ).bufferedWriter().use { writer ->

            writer.write(line)
        }
    }

    fun loadResults(): List<GameResult> {

        val file = context.getFileStreamPath(
            FILE_NAME
        )

        if (!file.exists()) {
            return emptyList()
        }

        return context.openFileInput(FILE_NAME)
            .bufferedReader()
            .useLines { lines ->

                lines.mapNotNull { line ->
                    parseResult(line)
                }.toList()
            }
    }

    private fun parseResult(
        line: String
    ): GameResult? {

        val parts = line.split("|")

        if (parts.size != 5) {
            return null
        }

        return try {

            GameResult(
                dateTime = parts[0],
                score = parts[1].toInt(),
                gridSize = parts[2].toInt(),
                gameTime = parts[3].toInt(),
                speedOption = parts[4].toInt()
            )

        } catch (_: NumberFormatException) {

            null
        }
    }
}