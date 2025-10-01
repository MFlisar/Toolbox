package com.michaelflisar.toolbox.app.features.feedback

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import io.github.vinceglb.filekit.PlatformFile

expect object FeedbackManager {

    val supported: Boolean

    fun sendFeedback(fileLoggerSetup: IFileLoggingSetup?, attachments: List<PlatformFile>, appendLogFiles: Boolean)

    suspend fun sendRelevantFiles()
}