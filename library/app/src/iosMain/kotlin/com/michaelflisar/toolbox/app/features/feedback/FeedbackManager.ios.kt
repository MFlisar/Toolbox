package com.michaelflisar.toolbox.app.features.feedback

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.sendFeedback
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.utils.toKotlinxPath

actual object FeedbackManager {

    actual val supported = true

    actual val supportsSendRelevantFile = false

    actual fun sendFeedback(
        appName: String,
        fileLoggerSetup: IFileLoggingSetup?,
        attachments: List<PlatformFile>,
        appendLogFiles: Boolean,
    ) {
        val appSetup = AppSetup.get()
        L.sendFeedback(
            appName = appName,
            appVersion = appSetup.appData.versionName,
            fileLoggingSetup = fileLoggerSetup,
            files = attachments.map {
                it.nsUrl.toKotlinxPath()
            },
            appendLogFile = appendLogFiles
        )
    }

    actual suspend fun sendRelevantFiles(appName: String) {
        // TODO: möglich???
    }
}