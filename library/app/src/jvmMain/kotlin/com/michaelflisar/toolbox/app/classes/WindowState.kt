package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.platform.AppPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.nio.file.AccessDeniedException

fun WindowState.reset(
    placement: Boolean,
    position: Boolean,
    size: Boolean,
) {
    val prefs = CommonApp.setup.prefs
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

fun WindowState.resetWindowSize() = reset(
    placement = false,
    size = true,
    position = false
)

fun WindowState.resetWindowPosition() = reset(
    placement = false,
    size = false,
    position = true
)

@Composable
fun rememberJewelWindowState(
    prefs: AppPrefs,
): WindowState {

    val scope = rememberCoroutineScope()

    val windowWidth by prefs.windowWidth.collectAsStateNotNull()
    val windowHeight by prefs.windowHeight.collectAsStateNotNull()
    val windowX by prefs.windowX.collectAsStateNotNull()
    val windowY by prefs.windowY.collectAsStateNotNull()
    val windowPlacement by prefs.windowPlacement.collectAsStateNotNull()

    val size = DpSize(
        windowWidth.dp,
        windowHeight.dp,
    )
    val pos = WindowPosition(
        windowX.dp,
        windowY.dp
    )

    val state = rememberWindowState(
        placement = windowPlacement,
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
            } catch (e: AccessDeniedException) {
                // ignore - comes from androidx datastore...
                L.i(e)
            }
        }
    }
        .launchIn(scope)

    return state
}