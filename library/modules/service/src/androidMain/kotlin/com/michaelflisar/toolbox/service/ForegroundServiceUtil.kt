package com.michaelflisar.toolbox.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.core.content.ContextCompat
import com.michaelflisar.toolbox.isServiceRunning

object ForegroundServiceUtil {

    inline fun <reified T : Service> isRunning(context: Context) =
        context.isServiceRunning(T::class.java)

    inline fun <reified T : Service> start(
        context: Context,
        action: String = ServiceConstants.ACTION_START_SERVICE,
        block: Intent.() -> Unit
    ) {
        val intent = Intent(context, T::class.java)
            .apply(block)
            .apply { this.action = action }
        ContextCompat.startForegroundService(context, intent)
    }

    inline fun <reified T : Service> stop(context: Context) {
        val intent = Intent(context, T::class.java)
            .apply { action = ServiceConstants.ACTION_STOP_SERVICE }
        ContextCompat.startForegroundService(context, intent)
    }

    // mode... Context.BIND_AUTO_CREATE oder 0
    inline fun <reified T : Service> bind(
        context: Context,
        connection: ServiceConnection,
        mode: Int
    ) {
        val intent = Intent(context, T::class.java)
        context.bindService(intent, connection, mode)
    }

    fun unbind(context: Context, connection: ServiceConnection) {
        context.unbindService(connection)
    }
}