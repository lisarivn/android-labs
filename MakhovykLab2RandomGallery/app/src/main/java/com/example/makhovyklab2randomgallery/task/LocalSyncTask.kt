
package com.example.makhovyklab2randomgallery.task

import android.content.Context
import android.util.Log
import com.example.makhovyklab2randomgallery.data.FileGalleryStorage
import java.util.concurrent.Callable

class LocalSyncTask(
    private val context: Context,
    private val storage: FileGalleryStorage,
    private val onProgress: (Int) -> Unit
) : Callable<Int> {

    companion object {
        private const val TAG = "LocalSyncTask"
    }

    override fun call(): Int {
        val demoFiles = context.assets
            .list("demo_gallery")
            ?.filter { it.endsWith(".jpg", ignoreCase = true) }
            ?: emptyList()

        if (demoFiles.isEmpty()) {
            throw IllegalStateException(
                "No demo photos found in assets/demo_gallery"
            )
        }

        val existingFiles = storage.getAll()

        val timestamp = System.currentTimeMillis()

        var copiedCount = 0

        demoFiles.forEachIndexed { index, fileName ->
            val photoNumber = index + 1

            val exists = existingFiles.any {
                it.endsWith("___demo_$photoNumber")
            }

            if (!exists) {
                val identifier =
                    "stable_${timestamp}___demo_$photoNumber"

                val name = fileName.substringBeforeLast(".")

                context.assets.open("demo_gallery/$fileName").use { input ->
                    storage.write(identifier, name).use { output ->
                        input.copyTo(output)
                    }
                }

                copiedCount++
            }

            val percentage =
                ((index + 1) * 100) / demoFiles.size

            onProgress(percentage)

            Log.d(
                TAG,
                "Processed $fileName: $percentage% " +
                        "on ${Thread.currentThread().name}"
            )
        }

        if (copiedCount > 0) {
            storage.setTimestamp(timestamp)
        }

        return copiedCount
    }
}
