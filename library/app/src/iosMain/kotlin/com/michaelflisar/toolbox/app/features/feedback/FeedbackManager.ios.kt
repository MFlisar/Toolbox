package com.michaelflisar.toolbox.app.features.feedback

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getAllExistingLogFiles
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.sendFeedback
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import com.michaelflisar.toolbox.zip.JavaZipManager
import com.michaelflisar.toolbox.zip.zipToCache
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.io.files.Path
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import io.github.vinceglb.filekit.utils.toKotlinxPath

actual object FeedbackManager {

    actual val supported = true

    actual val supportsSendRelevantFile = false

    actual fun sendFeedback(
        appName: String,
        fileLoggerSetup: IFileLoggingSetup?,
        attachments: List<PlatformFile>,
        appendLogFiles: Boolean
    ) {
        val appSetup = AppSetup.get()
        L.sendFeedback(
            appName = appName,
            appVersion = appSetup.versionName,
            fileLoggingSetup = fileLoggerSetup,
            files = attachments.map {
                it.nsUrl.toKotlinxPath()
            }
            ,
            appendLogFile = appendLogFiles
        )
    }

    actual suspend fun sendRelevantFiles(appName: String) {
        // TODO: möglich???
    }
}