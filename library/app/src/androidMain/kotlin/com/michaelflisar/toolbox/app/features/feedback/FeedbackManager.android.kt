package com.michaelflisar.toolbox.app.features.feedback

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getAllExistingLogFiles
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.sendFeedback
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import com.michaelflisar.toolbox.zip.JavaZipManager
import com.michaelflisar.toolbox.zip.zipToCache
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import java.io.File

actual object FeedbackManager {

    actual val supported = true

    actual fun sendFeedback(
        fileLoggerSetup: IFileLoggingSetup?,
        attachments: List<PlatformFile>,
        appendLogFiles: Boolean
    ) {
        L.sendFeedback(
            fileLoggingSetup = fileLoggerSetup,
            files = attachments.map {
                when (val af = it.androidFile) {
                    is AndroidFile.FileWrapper -> af.file
                    is AndroidFile.UriWrapper -> File(af.uri.path!!) // wir erwarten hier nur echte files!
                }
            },
            appendLogFile = appendLogFiles
        )
    }

    actual suspend fun sendRelevantFiles() {
        val context = AppContext.context()
        val fileLoggerSetup = AppSetup.get().fileLogger?.setup
        val folderDatabase = context.getDatabasePath("data.db").parentFile
        val file1: JavaZipFileContent? = folderDatabase?.let { JavaZipFileContent.Folder(it, "database") }
        val files2: List<JavaZipFileContent>? = fileLoggerSetup?.getAllExistingLogFiles()?.map { JavaZipFileContent.Folder(it, "logs/${it.name}") }
        val files: List<JavaZipFileContent> = listOfNotNull(file1) + (files2 ?: emptyList())
        val zipFile = JavaZipManager.zipToCache(context, files) ?: return
        sendFeedback(fileLoggerSetup, listOfNotNull(PlatformFile(zipFile)), false)
    }
}