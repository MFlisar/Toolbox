package com.michaelflisar.toolbox.app.features.feedback

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import io.github.vinceglb.filekit.PlatformFile

actual object FeedbackManager {

    actual val supported = false

    actual fun sendFeedback(
        fileLoggerSetup: IFileLoggingSetup?,
        attachments: List<PlatformFile>,
        appendLogFiles: Boolean
    ) {
        // --
    }

    actual suspend fun sendRelevantFiles() {
        // --
    }
}