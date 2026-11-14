package com.michaelflisar.toolbox.backup.worker

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.backup.BackupDefaults
import com.michaelflisar.toolbox.backup.R
import com.michaelflisar.toolbox.backup.internal.BackupServiceUtil
import com.michaelflisar.toolbox.service.BaseWorker
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import io.github.vinceglb.filekit.PlatformFile
import kotlin.time.Duration.Companion.seconds

class RestoreWorker internal constructor(
    context: Context,
    workerParams: WorkerParameters,
) : BaseWorker<Throwable?>(context, workerParams) {

    companion object {

        private const val KEY_FILES = "files"
        private const val KEY_BACKUP_URI = "backup_uri"

        private const val DEFAULT_MANUAL_TAG = "manual_restore"

        private fun data(files: List<JavaZipFileContent>, backupUri: Uri) = workDataOf(
            KEY_FILES to files.map { BackupServiceUtil.JSON.encodeToString(it) }.toTypedArray(),
            KEY_BACKUP_URI to backupUri.toString()
        ).also {
            L.logIf { BackupDefaults.LOG }?.d {
                "RestoreWorker.data: prepared input, files=${files.size}"
            }
        }

        fun enqueueWorker(context: Context, files: List<JavaZipFileContent>, backupUri: Uri) {
            L.logIf { BackupDefaults.LOG }?.d {
                "RestoreWorker.enqueueWorker: scheduling restore, files=${files.size}"
            }
            BackupServiceUtil.enqueue(
                context,
                data(files, backupUri),
                false,
                0.seconds,
                DEFAULT_MANUAL_TAG
            )
        }

    }

    override val foreground = true
    override val setup = BackupServiceUtil.SERVICE_RESTORE_SETUP

    override suspend fun getForegroundInfo(): ForegroundInfo {
        L.logIf { BackupDefaults.LOG }?.d {
            "RestoreWorker.getForegroundInfo: creating foreground info, workerId=$id, notificationId=${setup.notificationId}"
        }
        return super.getForegroundInfo().also {
            L.logIf { BackupDefaults.LOG }?.d {
                "RestoreWorker.getForegroundInfo: foreground info created, workerId=$id"
            }
        }
    }

    override suspend fun onInitNotification(builder: NotificationCompat.Builder) {
        L.logIf { BackupDefaults.LOG }?.d {
            "RestoreWorker.onInitNotification: initializing notification, workerId=$id"
        }
        val title = "App Restore"
        val info = "Restoring app..."
        builder
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(info)
    }

    override suspend fun onPrepareKeptNotification(result: Throwable?) {
        L.logIf { BackupDefaults.LOG }?.d {
            "RestoreWorker.onPrepareKeptNotification: preparing completion notification, workerId=$id, succeeded=${result == null}"
        }
        val title = "App Restore"
        val info = if (result == null) "Done" else "Failed: ${result.message}"
        getBuilder()
            .setContentTitle(title)
            .setContentText(info)
    }

    override suspend fun run(): Throwable? {

        L.logIf { BackupDefaults.LOG }?.d {
            "RestoreWorker.run: started, workerId=$id, managerRegistered=${BackupManager.manager != null}"
        }
        val manager = BackupManager.manager!!

        val files = inputData.getStringArray(KEY_FILES)!!
            .map { BackupServiceUtil.JSON.decodeFromString<JavaZipFileContent>(it) }
        val backupUri = Uri.parse(inputData.getString(KEY_BACKUP_URI))
        L.logIf { BackupDefaults.LOG }?.d {
            "RestoreWorker.run: input decoded, workerId=$id, files=${files.size}"
        }
        L.logIf { BackupDefaults.LOG }?.d {
            "RestoreWorker.run: starting restore operation, workerId=$id"
        }
        val result = manager.restore(files, PlatformFile(backupUri))
        L.logIf { BackupDefaults.LOG }?.d {
            "RestoreWorker.run: restore operation finished, workerId=$id, succeeded=${result == null}"
        }
        return result
    }
}