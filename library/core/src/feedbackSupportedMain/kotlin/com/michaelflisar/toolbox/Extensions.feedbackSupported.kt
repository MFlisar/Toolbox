package com.michaelflisar.toolbox

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.FeedbackConfig
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import kotlinx.io.files.Path

fun L.sendFeedback(
    appName: String,
    appVersion: String,
    fileLoggingSetup: IFileLoggingSetup?,
    files: List<Path> = emptyList(),
    appendLogFile: Boolean = true,
) {
    L.sendFeedback(
        config = FeedbackConfig.create(
            receiver = Toolbox.MAIL,
            appName = appName,
            appVersion = appVersion
        ),
        attachments = files + (fileLoggingSetup?.getLatestLogFilePath()?.takeIf { appendLogFile }?.let {
            listOf(it)
        } ?: emptyList())
    )
}