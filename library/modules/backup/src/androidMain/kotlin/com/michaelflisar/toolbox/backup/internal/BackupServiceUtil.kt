package com.michaelflisar.toolbox.backup.internal

import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.NotificationUtil
import com.michaelflisar.toolbox.backup.worker.BackupWorker
import com.michaelflisar.toolbox.service.ServiceSetup
import kotlinx.serialization.json.Json

object BackupServiceUtil {

    internal val JSON = Json {
        useArrayPolymorphism = true
    }

    private const val NOTIFICATION_BACKUP_ID = 5000
    private const val NOTIFICATION_BACKUP_KEPT_ID = 5001

    private const val NOTIFICATION_RESTORE_ID = 5002
    private const val NOTIFICATION_RESTORE_KEPT_ID = 5003

    const val NOTIFICATION_CHANNEL_BACKUP_ID = "backup_channel"
    const val NOTIFICATION_CHANNEL_BACKUP_NAME = "Backup Notifications"

    private val CHANNEL = ServiceSetup.Channel(
        id = NOTIFICATION_CHANNEL_BACKUP_ID,
        name = NOTIFICATION_CHANNEL_BACKUP_NAME,
        importance = NotificationManagerCompat.IMPORTANCE_MAX
    )

    fun createChannels() {
        if (!NotificationUtil.channelExists(AppContext.context(), NOTIFICATION_CHANNEL_BACKUP_ID)) {
            NotificationUtil.createChannel(
                AppContext.context(),
                NOTIFICATION_CHANNEL_BACKUP_NAME,
                NOTIFICATION_CHANNEL_BACKUP_ID,
                NotificationManagerCompat.IMPORTANCE_MAX
            )
        }
    }


    fun enqueue(context: Context, inputData: Data, needsInternet: Boolean) {
        val workManager = WorkManager.getInstance(context)
        val constraints = Constraints.Builder()
            .apply {
                if (needsInternet) {
                    setRequiredNetworkType(NetworkType.CONNECTED)
                }
            }
            .build()
        val backupRequest = OneTimeWorkRequestBuilder<BackupWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        workManager.enqueue(backupRequest)
    }

    val SERVICE_BACKUP_SETUP = ServiceSetup(
        channel = CHANNEL,
        notificationId = NOTIFICATION_BACKUP_ID,
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else 0,
        keptNotificationId = NOTIFICATION_BACKUP_KEPT_ID
    )

    val SERVICE_RESTORE_SETUP = ServiceSetup(
        channel = CHANNEL,
        notificationId = NOTIFICATION_RESTORE_ID,
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else 0,
        keptNotificationId = NOTIFICATION_RESTORE_KEPT_ID
    )

}