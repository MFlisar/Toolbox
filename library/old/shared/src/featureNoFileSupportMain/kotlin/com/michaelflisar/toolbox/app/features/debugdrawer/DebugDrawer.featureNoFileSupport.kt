package com.michaelflisar.toolbox.app.features.debugdrawer

import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup

@androidx.compose.runtime.Composable
internal actual fun DebugDrawerLumberjack(
    drawerState: DebugDrawerState,
    setup: IFileLoggingSetup,
    mailReceiver: String
) {
    // not supported in this platform
}