package com.michaelflisar.toolbox.app.features.feedback

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import io.github.vinceglb.filekit.PlatformFile

actual object FeedbackManager {

    actual val supported = false // Anhänge gehen auf Windows eigentlich nicht, macht daher keinen Sinn...

    actual fun sendFeedback(
        appName: String,
        fileLoggerSetup: IFileLoggingSetup?,
        attachments: List<PlatformFile>,
        appendLogFiles: Boolean
    ) {
        // --
    }

    actual suspend fun sendRelevantFiles(appName: String) {
        // --
    }
}