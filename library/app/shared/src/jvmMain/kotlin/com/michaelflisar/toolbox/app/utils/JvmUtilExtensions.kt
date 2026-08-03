package com.michaelflisar.toolbox.app.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.WindowExceptionHandler
import androidx.compose.ui.window.WindowExceptionHandlerFactory
import com.michaelflisar.toolbox.app.features.dialogs.JvmCrashDialog
import com.michaelflisar.toolbox.utils.JvmInfo
import com.michaelflisar.toolbox.utils.JvmUtil
import kotlin.system.exitProcess

@OptIn(ExperimentalComposeUiApi::class)
fun JvmUtil.createWindowExceptionHandlerFactory() =
    WindowExceptionHandlerFactory { window ->
        println("WINDOW HANDLER FACTORY")
        WindowExceptionHandler { exception ->
            println("WINDOW HANDLER")
            JvmCrashDialog.showExceptionDialog(
                title = "Exception",
                throwable = exception,
                infos = JvmCrashDialog.getDefaultInfos(),
            )
            exitProcess(1)
        }
    }

fun JvmUtil.runApp(
    title: String = "Exception",
    infos: List<JvmInfo> = JvmCrashDialog.getDefaultInfos(),
    block: () -> Unit,
) {
    try {
        block()
    } catch (e: Throwable) {
        println("Crash in main block")
        JvmCrashDialog.showExceptionDialog(
            title = title,
            throwable = e,
            infos = infos,
        )
    }
}