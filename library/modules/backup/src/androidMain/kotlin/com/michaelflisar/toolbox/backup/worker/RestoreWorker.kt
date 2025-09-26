package com.michaelflisar.toolbox.backup.worker

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.michaelflisar.toolbox.backup.AndroidBackupManager
import com.michaelflisar.toolbox.backup.internal.BackupServiceUtil
import com.michaelflisar.toolbox.backup.R
import com.michaelflisar.toolbox.service.BaseWorker
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import io.github.vinceglb.filekit.PlatformFile

class RestoreWorker internal constructor(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker<Throwable?>(context, workerParams) {

    companion object {

        private const val KEY_FILES = "files"
        private const val KEY_BACKUP_URI = "backup_uri"

        private fun data(files: List<JavaZipFileContent>, backupUri: Uri) = workDataOf(
            KEY_FILES to files.map { BackupServiceUtil.JSON.encodeToString(it) }.toTypedArray(),
            KEY_BACKUP_URI to backupUri.toString()
        )

        fun enqueueWorker(context: Context, files: List<JavaZipFileContent>, backupUri: Uri, needsInternet: Boolean) {
            BackupServiceUtil.enqueue(context, data(files, backupUri), needsInternet)
        }

    }

    override val foreground = true
    override val setup = BackupServiceUtil.SERVICE_RESTORE_SETUP

    override fun onInitNotification(builder: NotificationCompat.Builder) {
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
        val title = "App Restore"
        val info = if (result == null) "Done" else "Failed: ${result.message}"
        builder
            .setContentTitle(title)
            .setContentText(info)
    }

    override suspend fun run(): Throwable? {
        val files = inputData.getStringArray(KEY_FILES)!!.map { BackupServiceUtil.JSON.decodeFromString<JavaZipFileContent>(it) }
        val backupUri = Uri.parse(inputData.getString(KEY_BACKUP_URI))
        return AndroidBackupManager.restore(files, PlatformFile(backupUri))
    }
}