package com.michaelflisar.toolbox.service

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.NotificationUtil
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.logIf
import kotlinx.coroutines.launch

abstract class BaseForegroundService<Result> : LifecycleService() {

    // ----------------------------
    // Abstrakte Parameter / Funktionen
    // ----------------------------

    protected lateinit var builder: NotificationCompat.Builder

    abstract val setup: ServiceSetup

    abstract suspend fun doWork(intent: Intent): Result
    abstract fun stopWork(intent: Intent): Result


    abstract fun onInitNotification(builder: NotificationCompat.Builder)

    abstract fun onPrepareKeptNotification(result: Result)

    // ------------
    // Service
    // ------------

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        L.logIf(ToolboxLogging.Tag.Service)?.d { "onCreate" }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        L.logIf(ToolboxLogging.Tag.Service)?.d { "onStartCommand" }
        intent?.let {
            when (it.action) {
                ServiceConstants.ACTION_START_SERVICE -> {
                    createNotification(::onInitNotification)
                    startForegroundService()
                    lifecycleScope.launch {
                        val result = doWork(intent)
                        if (setup.keptNotificationId != null) {
                            onPrepareKeptNotification(result)
                            createKeepNotification()
                        }
                        stopForegroundService()
                        stopService()
                    }
                }

                ServiceConstants.ACTION_STOP_SERVICE -> {
                    stopWork(it)
                    stopService()
                }

                else -> stopService()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        L.logIf(ToolboxLogging.Tag.Service)?.d { "onBind" }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        L.logIf(ToolboxLogging.Tag.Service)?.d { "onUnbind" }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        L.logIf(ToolboxLogging.Tag.Service)?.d { "onDestroy" }
        super.onDestroy()
    }

    // ------------
    // Notification
    // ------------

    private fun createNotification(block: NotificationCompat.Builder.() -> Unit) {
        builder = NotificationCompat.Builder(this, setup.channel.id)
            .apply(block)
            .apply {
                setOnlyAlertOnce(true)
                setOngoing(true)
            }
    }

    protected fun updateNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(setup.notificationId, builder.build())
    }

    private fun createKeepNotification() {
        builder
            .apply {
                setOngoing(false)
                setProgress(0, 0, false)
            }
    }

    // ------------
    // Functions
    // ------------

    private fun startForegroundService() {
        L.logIf(ToolboxLogging.Tag.Service)?.d { "startForegroundService" }
        NotificationUtil.createChannelIfNecessary(
            this,
            setup.channel.name,
            setup.channel.id,
            setup.channel.importance
        )
        ServiceCompat.startForeground(
            this,
            setup.notificationId,
            builder.build(),
            setup.type
        )
    }

    private fun stopForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        L.logIf(ToolboxLogging.Tag.Service)?.d { "stopForegroundService" }
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        L.logIf(ToolboxLogging.Tag.Service)?.d { "keep notification: ${setup.keptNotificationId}" }
        if (setup.keptNotificationId != null) {
            notificationManager.notify(setup.keptNotificationId!!, builder.build())
        }
    }

    private fun stopService() {
        L.logIf(ToolboxLogging.Tag.Service)?.d { "stopService" }
        stopSelf()
    }

    // ------------
    // Notification
    // ------------

    // ------------
    // Binder
    // ------------

    inner class LocalBinder : Binder() {
        fun getService(): BaseForegroundService<*> = this@BaseForegroundService
    }

    abstract class Connection<Service> : ServiceConnection {

        var service: Service? = null
            private set
        var bound: Boolean = false
            private set

        private val callbacks = ArrayList<(connected: Boolean) -> Unit>()

        fun addCallback(callback: (connected: Boolean) -> Unit) {
            callbacks.add(callback)
        }

        fun removeCallback(callback: (connected: Boolean) -> Unit) {
            callbacks.remove(callback)
        }

        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val b = binder as BaseForegroundService<*>.LocalBinder
            @Suppress("UNCHECKED_CAST")
            service = b.getService() as Service
            bound = true
            callbacks.forEach { it(true) }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
            callbacks.forEach { it(false) }
            service = null
        }
    }
}