package com.michaelflisar.toolbox

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.app.NotificationManagerCompat
import com.michaelflisar.lumberjack.core.L
import kotlin.collections.find
import kotlin.collections.indices
import kotlin.let
import kotlin.ranges.reversed

object NotificationUtil {

    fun openSettings(context: Context) {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
        context.startActivity(intent)
    }

    fun deleteAllChannels(context: Context) {
        if (supportsChannels()) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channels = notificationManager.notificationChannels
            try {
                for (i in channels.indices.reversed()) {
                    notificationManager.deleteNotificationChannel(channels[i].id)
                }
                L.logIf(ToolboxLogging.Tag.None)?.v { "Notification channels cleaned" }
            } catch (e: Exception) {
                L.e(e)
            }
        }
    }

    fun initChannel(
        context: Context,
        channelId: String,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_LOW,
    ) {
        if (!channelExists(context, channelId)) {
            createChannel(
                context,
                channelName,
                channelId,
                importance
            )
        }
    }

    fun channelExists(context: Context, id: String): Boolean {
        if (supportsChannels()) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channels = notificationManager.notificationChannels
            return channels.find { it.id == id }?.let { true } ?: false
        }
        return true
    }

    fun createChannel(
        context: Context,
        channelName: String,
        channelId: String,
        importance: Int,
        sound: Boolean = true,
        vibrate: Boolean = true,
        lights: Boolean = true
    ) : Boolean {
        return if (supportsChannels()) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(lights)
            notificationChannel.enableVibration(vibrate)
            if (!sound) {
                notificationChannel.setSound(null, null)
            }
            notificationManager.createNotificationChannel(notificationChannel)
            true
        } else false
    }

    fun createChannelIfNecessary(
        context: Context,
        channelName: String,
        channelId: String,
        importance: Int = NotificationManagerCompat.IMPORTANCE_HIGH,
        sound: Boolean = true,
        vibrate: Boolean = true,
        lights: Boolean = true
    ): Boolean {
        return if (!channelExists(context, channelId)) {
            createChannel(context, channelName, channelId, importance, sound, vibrate, lights)
        } else false
    }

    fun deleteChannel(context: Context, channelId: String) {
        if (supportsChannels()) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            try {
                notificationManager.deleteNotificationChannel(channelId)
            } catch (e: Exception) {
                // channel existierte nicht mehr...
            }
        }
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun supportsChannels() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}