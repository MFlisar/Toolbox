package com.michaelflisar.toolbox.app.features.debugdrawer

import androidx.compose.runtime.Composable
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup

@Composable
internal actual fun DebugDrawerLumberjack(
    drawerState: DebugDrawerState,
    setup: IFileLoggingSetup,
    mailReceiver: String
) {
    com.michaelflisar.composedebugdrawer.plugin.lumberjack.DebugDrawerLumberjack(drawerState, setup, mailReceiver)
}