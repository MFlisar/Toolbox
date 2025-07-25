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
import kotlinx.coroutines.launch

abstract class BaseForegroundService<Result> : LifecycleService() {

    // ----------------------------
    // Abstrakte Parameter / Funktionen
    // ----------------------------

    protected lateinit var builder: NotificationCompat.Builder

    abstract val setup: ServiceSetup

    abstract suspend fun run(intent: Intent): Result
    abstract fun onInitNotification(builder: NotificationCompat.Builder)

    abstract fun onPrepareKeptNotification(result: Result)

    // ------------
    // Service
    // ------------

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        L.logIf { ServiceConstants.DEBUG }?.d { "onCreate" }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        L.logIf { ServiceConstants.DEBUG }?.d { "onStartCommand" }
        intent?.let {
            when (it.action) {
                ServiceConstants.ACTION_START_SERVICE -> {
                    doWork(it)
                }

                ServiceConstants.ACTION_STOP_SERVICE -> {
                    stopService()
                }

                else -> stopService()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        L.logIf { ServiceConstants.DEBUG }?.d { "onBind" }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        L.logIf { ServiceConstants.DEBUG }?.d { "onUnbind" }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        L.logIf { ServiceConstants.DEBUG }?.d { "onDestroy" }
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

    private fun doWork(intent: Intent) {
        createNotification(::onInitNotification)
        startForegroundService()
        lifecycleScope.launch {
            val result = run(intent)
            if (setup.keptNotificationId != null) {
                onPrepareKeptNotification(result)
                createKeepNotification()
            }
            stopForegroundService()
            stopService()
        }
    }

    private fun startForegroundService() {
        L.logIf { ServiceConstants.DEBUG }?.d { "startForegroundService" }
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
        L.logIf { ServiceConstants.DEBUG }?.d { "stopForegroundService" }
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        L.logIf { ServiceConstants.DEBUG }?.d { "keep notification: ${setup.keptNotificationId}" }
        if (setup.keptNotificationId != null) {
            notificationManager.notify(setup.keptNotificationId!!, builder.build())
        }
    }

    private fun stopService() {
        L.logIf { ServiceConstants.DEBUG }?.d { "stopService" }
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