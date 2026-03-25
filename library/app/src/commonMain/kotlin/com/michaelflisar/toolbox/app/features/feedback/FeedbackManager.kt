package com.michaelflisar.toolbox.app.features.feedback

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import io.github.vinceglb.filekit.PlatformFile

expect object FeedbackManager {

    val supported: Boolean

    val supportsSendRelevantFile: Boolean

    fun sendFeedback(appName: String, fileLoggerSetup: IFileLoggingSetup?, attachments: List<PlatformFile>, appendLogFiles: Boolean)

    suspend fun sendRelevantFiles(appName: String)
}