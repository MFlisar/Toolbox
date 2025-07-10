package com.michaelflisar.toolbox.app.features.debugdrawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.michaelflisar.composedebugdrawer.buildinfos.DebugDrawerBuildInfos
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerButton
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerInfo
import com.michaelflisar.toolbox.app.Constants
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal actual val supportsBuildAndDeviceInfos: Boolean = true

@Composable
internal actual fun DebugDrawerBuildInfos(
    drawerState: DebugDrawerState,
    scope: CoroutineScope,
    prefs: DebugPrefs
) {
    DebugDrawerBuildInfos(
        image = { Icon(Icons.Default.Info, null) },
        drawerState = drawerState
    ) {
        DebugDrawerInfo(title = "Author", info = Constants.DEVELOPER_NAME)
        DebugDrawerButton(label = "Disable Drawer") {
            scope.launch {
                drawerState.drawerState.close()
                prefs.showDebugDrawer.update(false)
            }
        }
    }
}

@Composable
internal actual fun DebugDrawerDeviceInfos(drawerState: DebugDrawerState) {
    com.michaelflisar.composedebugdrawer.deviceinfos.DebugDrawerDeviceInfos(drawerState = drawerState)
}