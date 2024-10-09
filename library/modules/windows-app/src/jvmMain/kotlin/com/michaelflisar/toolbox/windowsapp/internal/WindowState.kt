package com.michaelflisar.toolbox.windowsapp.internal

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.windowsapp.classes.AppState
import com.michaelflisar.toolbox.windowsapp.prefs.DesktopAppPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@Composable
internal fun rememberWindowState() : WindowState {

    val scope = rememberCoroutineScope()
    val size = DpSize(
        DesktopAppPrefs.windowWidth.value.dp,
        DesktopAppPrefs.windowHeight.value.dp,
    )
    val pos = WindowPosition(
        DesktopAppPrefs.windowX.value.dp,
        DesktopAppPrefs.windowY.value.dp
    )
    val placement = DesktopAppPrefs.windowPlacement.value

    val state = rememberWindowState(
        placement = placement,
        position = pos,
        size = size
    )

    snapshotFlow { state.size }
        .onEach {
            withContext(Dispatchers.IO) {
                DesktopAppPrefs.windowWidth.update(it.width.value.toInt())
                DesktopAppPrefs.windowHeight.update(it.height.value.toInt())
            }
        }
        .launchIn(scope)
    snapshotFlow { state.position }
        .onEach {
            withContext(Dispatchers.IO) {
                DesktopAppPrefs.windowX.update(it.x.value.toInt())
                DesktopAppPrefs.windowY.update(it.y.value.toInt())
            }
        }
        .launchIn(scope)
    snapshotFlow { state.placement }
        .onEach {
            withContext(Dispatchers.IO) {
                DesktopAppPrefs.windowPlacement.update(it)
            }
        }
        .launchIn(scope)

    return state
}