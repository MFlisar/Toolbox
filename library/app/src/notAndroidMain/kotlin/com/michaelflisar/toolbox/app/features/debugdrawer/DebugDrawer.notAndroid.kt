package com.michaelflisar.toolbox.app.features.debugdrawer

import androidx.compose.runtime.Composable
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import kotlinx.coroutines.CoroutineScope

internal actual val supportsBuildAndDeviceInfos: Boolean = false

@Composable
internal actual fun DebugDrawerBuildInfos(drawerState: DebugDrawerState, scope: CoroutineScope, prefs: DebugPrefs) {
    // This function is not supported on this platform
}

@Composable
internal actual fun DebugDrawerDeviceInfos(drawerState: DebugDrawerState) {
    // This function is not supported on this platform
}