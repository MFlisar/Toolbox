package com.michaelflisar.toolbox.app.utils

import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.toolbox.app.features.dialogs.JvmCrashDialog
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.utils.JvmUtil

/**
 * Creates a file logger for the JVM.
 *
 * @param folder The folder where the log files will be stored.
 * @param fileName The base name of the log file. Default is "log".
 * @param fileExtension The extension of the log file. Default is "txt".
 * @param maxFileSizeInBytes The maximum size of the log file in bytes. Default is 5 MB.
 * @param trimFactor The factor by which the log file will be trimmed when it exceeds the maximum size. Default is 0.5 (50%).
 * @return A [FileLogger] instance configured with the specified parameters.
 */
fun JvmUtil.createFileLogger(
    folder: String,
    fileName: String = FileLoggerSetup.DEFAULT_FILE_BASE_NAME,
    fileExtension: String = FileLoggerSetup.DEFAULT_FILE_EXTENSION,
    maxFileSizeInBytes: Int = FileLoggerSetup.DEFAULT_MAX_FILE_SIZE_IN_BYTES,
    trimFactor: Float = 0.5f,
): FileLogger<*> {
    return FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.SingleFile(
            folder = folder,
            fileName = fileName,
            fileExtension = fileExtension,
            maxFileSizeInBytes = maxFileSizeInBytes,
            trimFactor = trimFactor
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
}

fun JvmUtil.runApp(
    title: String = "Exception",
    block: () -> Unit,
) {
    try {
        block()
    } catch (e: Throwable) {
        JvmCrashDialog.showExceptionDialog(
            title = title,
            throwable = e
        )
    }
}