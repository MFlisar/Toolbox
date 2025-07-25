package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup

@Composable
internal actual fun LumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    setup: IFileLoggingSetup
) {
    // not supported in this platform
}