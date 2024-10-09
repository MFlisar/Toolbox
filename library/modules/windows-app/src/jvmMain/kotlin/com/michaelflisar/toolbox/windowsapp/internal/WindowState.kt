package com.michaelflisar.toolbox.windowsapp.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.toolbox.windowsapp.prefs.DesktopAppPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@Composable
internal fun rememberWindowState(
    prefs: DesktopAppPrefs
): WindowState {

    val scope = rememberCoroutineScope()
    val size = DpSize(
        prefs.windowWidth.value.dp,
        prefs.windowHeight.value.dp,
    )
    val pos = WindowPosition(
        prefs.windowX.value.dp,
        prefs.windowY.value.dp
    )
    val placement = prefs.windowPlacement.value

    val state = rememberWindowState(
        placement = placement,
        position = pos,
        size = size
    )

    snapshotFlow { state.size }
        .onEach {
            withContext(Dispatchers.IO) {
                prefs.windowWidth.update(it.width.value.toInt())
                prefs.windowHeight.update(it.height.value.toInt())
            }
        }
        .launchIn(scope)
    snapshotFlow { state.position }
        .onEach {
            withContext(Dispatchers.IO) {
                prefs.windowX.update(it.x.value.toInt())
                prefs.windowY.update(it.y.value.toInt())
            }
        }
        .launchIn(scope)
    snapshotFlow { state.placement }
        .onEach {
            withContext(Dispatchers.IO) {
                prefs.windowPlacement.update(it)
            }
        }
        .launchIn(scope)

    return state
}