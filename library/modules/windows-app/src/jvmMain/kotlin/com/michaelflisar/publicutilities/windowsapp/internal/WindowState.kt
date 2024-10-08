package com.michaelflisar.publicutilities.windowsapp.internal

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.publicutilities.windowsapp.DesktopApp
import com.michaelflisar.publicutilities.windowsapp.classes.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun rememberWindowState(
    scope: CoroutineScope,
    appState: AppState
) : WindowState {

    val size = DpSize(
        DesktopApp.Constants.WINDOW_WIDTH.getState(appState).value.dp,
        DesktopApp.Constants.WINDOW_HEIGHT.getState(appState).value.dp,
    )
    val pos = WindowPosition(
        DesktopApp.Constants.WINDOW_X.getState(appState).value.dp,
        DesktopApp.Constants.WINDOW_Y.getState(appState).value.dp
    )
    val placement = WindowPlacement.entries[DesktopApp.Constants.WINDOW_PLACEMENT.getState(appState).value]

    val state = rememberWindowState(
        placement = placement,
        position = pos,
        size = size
    )

    snapshotFlow { state.size }
        .onEach {
            DesktopApp.Constants.WINDOW_WIDTH.updateValue(appState, it.width.value.toInt())
            DesktopApp.Constants.WINDOW_HEIGHT.updateValue(appState, it.height.value.toInt())
        }
        .launchIn(scope)
    snapshotFlow { state.position }
        .onEach {
            DesktopApp.Constants.WINDOW_X.updateValue(appState, it.x.value.toInt())
            DesktopApp.Constants.WINDOW_Y.updateValue(appState, it.y.value.toInt())
        }
        .launchIn(scope)
    snapshotFlow { state.placement }
        .onEach {
            DesktopApp.Constants.WINDOW_PLACEMENT.updateValue(appState, it.ordinal)
        }
        .launchIn(scope)

    return state
}