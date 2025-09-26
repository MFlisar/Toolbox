package com.michaelflisar.toolbox.service

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters

abstract class BaseWorker<Data>(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private var _builder: NotificationCompat.Builder? = null
    protected val builder: NotificationCompat.Builder
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        get() {
            if (_builder == null) {
                createNotification(::onInitNotification, false)
            }
            return _builder!!
        }

    abstract val setup: ServiceSetup
    abstract val foreground: Boolean

    abstract fun onInitNotification(builder: NotificationCompat.Builder)
    abstract suspend fun onPrepareKeptNotification(result: Data)

    abstract suspend fun run(): Data

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    final override suspend fun doWork(): Result {
        if (cancelWork())
            return Result.success()
        createNotification(::onInitNotification, true)
        if (foreground)
            setForeground(getForegroundInfo())
        val result = run()
        removeNotification()
        if (setup.keptNotificationId != null) {
            prepareFinalNotification()
            onPrepareKeptNotification(result)
            showKeptNotification(result)
        }
        return createResult(result)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(setup.notificationId, builder.build(), setup.type)
        } else {
            ForegroundInfo(setup.notificationId, builder.build())
        }
    }

    open fun createResult(result: Data): Result {
        return Result.success()
    }

    open fun cancelWork(): Boolean {
        return false
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    protected fun updateNotification() {
        showNotification()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun createNotification(block: NotificationCompat.Builder.() -> Unit, show: Boolean) {
        if (_builder == null) {
            _builder = NotificationCompat.Builder(applicationContext, setup.channel.id)
                .apply(block)
                .apply {
                    setOnlyAlertOnce(true)
                    setOngoing(true)
                }
        }
        if (show) {
            showNotification()
        }
    }

    private fun prepareFinalNotification() {
        builder.apply {
            setOngoing(false)
            setProgress(0, 0, false)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification() {
        NotificationManagerCompat.from(applicationContext)
            .notify(setup.notificationId, builder.build())
    }

    protected fun removeNotification() {
        NotificationManagerCompat.from(applicationContext).cancel(setup.notificationId)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    protected open fun showKeptNotification(result: Data) {
        NotificationManagerCompat.from(applicationContext)
            .notify(setup.keptNotificationId!!, builder.build())
    }
}