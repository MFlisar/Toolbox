package com.michaelflisar.toolbox.app.utils

import com.michaelflisar.toolbox.app.features.dialogs.JvmCrashDialog
import com.michaelflisar.toolbox.utils.JvmUtil

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