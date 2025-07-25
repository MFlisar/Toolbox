package com.michaelflisar.toolbox.service

import androidx.core.app.NotificationManagerCompat

class ServiceSetup(
    val channel: Channel,
    val notificationId: Int,
    val type: Int,
    val keptNotificationId: Int?
) {
    class Channel(
        val id: String,
        val name: String,
        val importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
    )
}