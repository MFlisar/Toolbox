package com.michaelflisar.toolbox.backup.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.backup.R
import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.internal.BackupServiceUtil
import com.michaelflisar.toolbox.service.BaseWorker
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.fromBookmarkData
import kotlin.time.Duration

class BackupWorker internal constructor(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker<Throwable?>(context, workerParams) {

    companion object {

        // keys
        private const val KEY_FILES = "files"
        private const val KEY_BACKUP_FOLDER_DATA = "backup_folder"

        // tags
        private const val DEFAULT_AUTO_TAG = "auto_backup"

        private fun data(
            files: List<JavaZipFileContent>,
            backupFolderData: ByteArray
        ) = workDataOf(
            KEY_FILES to files.map { BackupServiceUtil.JSON.encodeToString(it) }.toTypedArray(),
            KEY_BACKUP_FOLDER_DATA to backupFolderData
        )

        fun enqueueAutoWorker(
            context: Context,
            files: List<JavaZipFileContent>,
            backupFolderData: ByteArray,
            initialDelay: Duration
        ) {
            BackupServiceUtil.enqueue(
                context = context,
                inputData = data(
                    files,
                    backupFolderData
                ),
                needsInternet = false,
                initialDelay = initialDelay,
                tag = DEFAULT_AUTO_TAG
            )
        }

        fun cancelAutoWorker(context: Context) {
            BackupServiceUtil.cancelAllWorkByTag(context, DEFAULT_AUTO_TAG)
        }

    }

    override val foreground = true
    override val setup = BackupServiceUtil.SERVICE_BACKUP_SETUP

    override fun onInitNotification(builder: NotificationCompat.Builder) {
        val title = "App Backup"
        val info = "Backing up app..."
        builder
            .setContentTitle(title)
            .setTicker(title)
            // TODO: Setup/Manager
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(info)
    }

    override suspend fun onPrepareKeptNotification(result: Throwable?) {
        val title = "App Backup"
        val info = if (result == null) "Done" else "Failed: ${result.message}"
        builder
            .setContentTitle(title)
            .setContentText(info)
    }

    override suspend fun run(): Throwable? {

        val manager = BackupManager.manager!!

        val files = inputData.getStringArray(KEY_FILES)!!
            .map { BackupServiceUtil.JSON.decodeFromString<JavaZipFileContent>(it) }
        val backupFolderData = inputData.getByteArray(KEY_BACKUP_FOLDER_DATA)!!
        val backupFolder = PlatformFile.fromBookmarkData(backupFolderData)

        // TODO:
        val backupFileName = manager.getAutoBackupFileName()
        val backupFile = try {
            backupFolder / backupFileName
        } catch (e: Exception) {
            L.e(e)
            return e
        }

        // Android Solution
        // val backupFile = when (val af = backupFolder.androidFile) {
        //     is AndroidFile.FileWrapper -> backupFolder / backupFileName
        //     is AndroidFile.UriWrapper -> {
        //         val folder = DocumentFile.fromTreeUri(context, af.uri)
        //         val newFile = folder.createFile(mimeType, backupFileName)
        //         val androidFileWrapper = AndroidFile.UriWrapper(newFile.getUri())
        //         PlatformFile(androidFileWrapper)
        //     }
        // }

        val throwable = try {
            manager.backup(files, backupFile)
        } catch (e: Exception) {
            L.e(e)
            e
        }

        manager.onEnqueueNextAutoBackup()

        return throwable
    }
}