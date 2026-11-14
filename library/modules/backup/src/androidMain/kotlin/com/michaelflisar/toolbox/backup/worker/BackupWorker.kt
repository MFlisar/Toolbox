package com.michaelflisar.toolbox.backup.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.backup.BackupDefaults
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.backup.R
import com.michaelflisar.toolbox.backup.internal.BackupServiceUtil
import com.michaelflisar.toolbox.service.BaseWorker
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.fromBookmarkData
import kotlin.time.Duration

class BackupWorker internal constructor(
    context: Context,
    workerParams: WorkerParameters,
) : BaseWorker<Throwable?>(context, workerParams) {

    companion object {

        // keys
        private const val KEY_FILES = "files"
        private const val KEY_BACKUP_FOLDER_DATA = "backup_folder"

        // tags
        private const val DEFAULT_AUTO_TAG = "auto_backup"

        private fun data(
            files: List<JavaZipFileContent>,
            backupFolderData: ByteArray,
        ) = workDataOf(
            KEY_FILES to files.map { BackupServiceUtil.JSON.encodeToString(it) }.toTypedArray(),
            KEY_BACKUP_FOLDER_DATA to backupFolderData
        ).also {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupWorker.data: prepared input, files=${files.size}, folderBookmarkBytes=${backupFolderData.size}"
            }
        }

        fun enqueueAutoWorker(
            context: Context,
            files: List<JavaZipFileContent>,
            backupFolderData: ByteArray,
            initialDelay: Duration,
        ) {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupWorker.enqueueAutoWorker: scheduling, files=${files.size}, delay=$initialDelay"
            }
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
            L.logIf { BackupDefaults.LOG }?.d { "BackupWorker.cancelAutoWorker: cancelling automatic backups" }
            BackupServiceUtil.cancelAllWorkByTag(context, DEFAULT_AUTO_TAG)
        }

    }

    override val foreground = true
    override val setup = BackupServiceUtil.SERVICE_BACKUP_SETUP

    override suspend fun getForegroundInfo(): ForegroundInfo {
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.getForegroundInfo: creating foreground info, workerId=$id, notificationId=${setup.notificationId}"
        }
        return super.getForegroundInfo().also {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupWorker.getForegroundInfo: foreground info created, workerId=$id"
            }
        }
    }

    override suspend fun onInitNotification(builder: NotificationCompat.Builder) {
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.onInitNotification: initializing notification, workerId=$id"
        }
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
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.onPrepareKeptNotification: preparing completion notification, workerId=$id, succeeded=${result == null}"
        }
        val title = "App Backup"
        val info = if (result == null) "Done" else "Failed: ${result.message}"
        getBuilder()
            .setContentTitle(title)
            .setContentText(info)
    }

    override suspend fun run(): Throwable? {

        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.run: started, workerId=$id, managerRegistered=${BackupManager.manager != null}"
        }
        val manager = BackupManager.manager!!

        val files = inputData.getStringArray(KEY_FILES)!!
            .map { BackupServiceUtil.JSON.decodeFromString<JavaZipFileContent>(it) }
        val backupFolderData = inputData.getByteArray(KEY_BACKUP_FOLDER_DATA)!!
        val backupFolder = PlatformFile.fromBookmarkData(backupFolderData)
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.run: input decoded, workerId=$id, files=${files.size}, folderBookmarkBytes=${backupFolderData.size}"
        }

        val backupFileName = manager.getAutoBackupFileName()
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.run: destination filename generated, workerId=$id"
        }
        val backupFile = try {
            backupFolder / backupFileName
        } catch (e: Exception) {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupWorker.run: could not create destination, workerId=$id, error=${e.javaClass.simpleName}"
            }
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
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupWorker.run: starting backup operation, workerId=$id"
            }
            manager.backup(files, backupFile)
        } catch (e: Exception) {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupWorker.run: backup operation threw, workerId=$id, error=${e.javaClass.simpleName}"
            }
            L.e(e)
            e
        }

        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.run: backup operation finished, workerId=$id, succeeded=${throwable == null}"
        }
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.run: scheduling next automatic backup, workerId=$id"
        }
        manager.onEnqueueNextAutoBackup()
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupWorker.run: next automatic backup scheduling call completed, workerId=$id"
        }

        return throwable
    }
}