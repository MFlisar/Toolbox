package com.michaelflisar.toolbox.windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.windows.prefs.DesktopAppPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.nio.file.AccessDeniedException

fun WindowState.reset(
    prefs: DesktopAppPrefs,
    placement: Boolean = true,
    position: Boolean = true,
    size: Boolean = true
) {
    if (placement) {
        this.placement = prefs.windowPlacement.defaultValue
    }
    if (position) {
        this.position = WindowPosition(
            prefs.windowX.defaultValue.dp,
            prefs.windowY.defaultValue.dp
        )
    }
    if (size) {
        this.size = DpSize(
            prefs.windowWidth.defaultValue.dp,
            prefs.windowHeight.defaultValue.dp,
        )
    }
}

@Composable
fun rememberJewelWindowState(
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

    snapshotFlow {
        Triple(state.size, state.position, state.placement)
    }.onEach {
            withContext(Dispatchers.IO) {
                try {
                    prefs.windowWidth.update(it.first.width.value.toInt())
                    prefs.windowHeight.update(it.first.height.value.toInt())
                    prefs.windowX.update(it.second.x.value.toInt())
                    prefs.windowY.update(it.second.y.value.toInt())
                    prefs.windowPlacement.update(it.third)
                } catch(e: AccessDeniedException) {
                    // ignore - comes from androidx datastore...
                }
            }
        }
        .launchIn(scope)

    return state
}