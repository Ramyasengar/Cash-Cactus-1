package com.example.cashcactus.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

private const val CHANNEL_ID = "budget_alert_channel"

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Budget Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies when budget is exceeded"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

fun showBudgetNotification(context: Context, message: String) {

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("⚠ Budget Alert")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    NotificationManagerCompat.from(context)
        .notify(1001, builder.build())
}

