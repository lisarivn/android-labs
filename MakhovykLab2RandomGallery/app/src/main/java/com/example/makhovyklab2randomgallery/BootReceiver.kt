package com.example.makhovyklab2randomgallery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (
            intent.action !=
            Intent.ACTION_BOOT_COMPLETED
        ) {
            return
        }

        showNotification(context)
    }

    private fun showNotification(
        context: Context
    ) {
        prepareChannel(context)

        val checkIntent =
            Intent(
                context,
                ActionsHandlerService::class.java
            ).apply {
                action =
                    ActionsHandlerService.ACTION_CHECK_FOR_UPDATES

                putExtra(
                    ActionsHandlerService.EXTRA_FROM_NOTIFICATION,
                    true
                )
            }

        val cancelIntent =
            Intent(
                context,
                ActionsHandlerService::class.java
            ).apply {
                action =
                    ActionsHandlerService.ACTION_CANCEL
            }

        val checkPendingIntent =
            PendingIntent.getService(
                context,
                REQUEST_CHECK,
                checkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val cancelPendingIntent =
            PendingIntent.getService(
                context,
                REQUEST_CANCEL,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(
                    context.getString(R.string.random_gallery)
                )
                .setContentText(
                    context.getString(
                        R.string.notification_check_updates
                    )
                )
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)
                .addAction(
                    0,
                    context.getString(R.string.check),
                    checkPendingIntent
                )
                .addAction(
                    0,
                    context.getString(R.string.cancel),
                    cancelPendingIntent
                )
                .build()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat
            .from(context)
            .notify(
                NOTIFICATION_ID,
                notification
            )
    }

    private fun prepareChannel(
        context: Context
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.random_gallery),
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            val manager =
                context.getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    companion object {

        private const val CHANNEL_ID =
            "default"

        private const val NOTIFICATION_ID =
            1

        private const val REQUEST_CHECK =
            1

        private const val REQUEST_CANCEL =
            2
    }
}