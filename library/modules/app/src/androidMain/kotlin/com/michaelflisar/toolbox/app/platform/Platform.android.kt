package com.michaelflisar.toolbox.app.platform

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AndroidAppPrefs
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.findActivity
import com.michaelflisar.toolbox.killApp
import com.michaelflisar.toolbox.restartApp

actual typealias AppPrefs = AndroidAppPrefs

@Composable
actual fun Platform.localContext() = PlatformContext(LocalContext.current)

actual val Platform.fileLoggerSetup: IFileLoggingSetup?
    get() = FileLoggerSetup.Daily.create(
        context = AppContext.context(),
        fileExtension = "txt"
    )

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = ::showToast

actual val Platform.restart: ((context: PlatformContext) -> Unit)?
    get() = { context -> context.androidContext.findActivity().restartApp() }

actual val Platform.kill: ((context: PlatformContext) -> Unit)?
    get() = { context -> context.androidContext.findActivity().killApp() }

private fun showToast(
    message: String,
    duration: Int
) {
    Toast.makeText(
        AppContext.context(),
        message,
        duration
    ).show()
}