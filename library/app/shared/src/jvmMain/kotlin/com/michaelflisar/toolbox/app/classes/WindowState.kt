package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.kotpreferences.core.SettingsConverter
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.features.preferences.BaseDesktopPrefs
import com.michaelflisar.toolbox.app.utils.WindowUtil
import com.michaelflisar.toolbox.info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.AccessDeniedException
import kotlin.time.Duration.Companion.milliseconds

fun WindowState.resetAll(density: Density, window: ComposeWindow) {
    reset(density, window, true, true, true)
}

fun WindowState.reset(
    density: Density,
    window: ComposeWindow,
    placement: Boolean,
    position: Boolean,
    size: Boolean,
) {
    val prefs = DesktopAppSetup.get().prefs

    if (placement) {
        this.placement = prefs.windowState.defaultValue.windowPlacement
    }
    if (size) {
        this.size = DpSize(
            prefs.windowState.defaultValue.windowWidth.dp,
            prefs.windowState.defaultValue.windowHeight.dp,
        )
    }
    if (position) {
        val (x, y) = WindowUtil.calcCenteredPosition(window)
        this.position = WindowPosition(with(density) { x.toDp() }, with(density) { y.toDp() })
    }

    //prefs.windowState.update(JewelWindowState(this))
}

fun WindowState.resetWindowSize() {
    val prefs = DesktopAppSetup.get().prefs
    this.size = DpSize(
        prefs.windowState.defaultValue.windowWidth.dp,
        prefs.windowState.defaultValue.windowHeight.dp,
    )
    //prefs.windowState.update(JewelWindowState(this))
}

fun WindowState.resetWindowPosition(density: Density, window: ComposeWindow) = reset(
    density = density,
    window = window,
    placement = false,
    size = false,
    position = true
)

@OptIn(FlowPreview::class)
@Composable
fun rememberDesktopWindowState(
    prefs: BaseDesktopPrefs,
): WindowState {

    if (!DesktopAppSetup.get().rememberWindowState) {
        return remember {
            WindowState(
                size = DpSize(
                    DesktopWindowState.DEFAULT_WIDTH.dp,
                    DesktopWindowState.DEFAULT_HEIGHT.dp
                ),
                position = WindowPosition.Aligned(Alignment.Center)
            )
        }
    }

    val windowState by prefs.windowState.collectAsStateNotNull()
    val state = remember(windowState) { windowState.toWindowState() }

    LaunchedEffect(state) {
        snapshotFlow { DesktopWindowState(state) }
            .distinctUntilChanged()
            .debounce(500.milliseconds)
            .collect {
                L.info(ToolboxLogging.Tag.Window) { "Saving window state: $it" }
                withContext(Dispatchers.IO) {
                    try {
                        prefs.windowState.update(it)
                    } catch (e: AccessDeniedException) {
                        // ignore - comes from androidx datastore...
                        L.info(ToolboxLogging.Tag.Window, t = e)
                    }
                }
            }
    }
    return state
}

@Serializable
data class DesktopWindowState(
    val windowWidth: Int = DEFAULT_WIDTH,
    val windowHeight: Int = DEFAULT_HEIGHT,
    val windowX: Int = 0,
    val windowY: Int = 0,
    val windowPlacement: WindowPlacement = WindowPlacement.Floating,
) {
    companion object {
        val DEFAULT_WIDTH = 1024
        val DEFAULT_HEIGHT = 800
    }

    constructor(windowState: WindowState) : this(
        windowWidth = windowState.size.width.value.toInt(),
        windowHeight = windowState.size.height.value.toInt(),
        windowX = windowState.position.x.value.toInt(),
        windowY = windowState.position.y.value.toInt(),
        windowPlacement = windowState.placement
    )

    fun toWindowState(): WindowState {
        return WindowState(
            placement = windowPlacement,
            position = WindowPosition(windowX.dp, windowY.dp),
            size = DpSize(windowWidth.dp, windowHeight.dp)
        )
    }

    object CONVERTER : SettingsConverter<DesktopWindowState, String> {
        override fun from(data: String): DesktopWindowState = Json.decodeFromString(data)
        override fun to(data: DesktopWindowState): String = Json.encodeToString(data)
    }

    override fun toString(): String {
        return "{w=$windowWidth, h=$windowHeight, x=$windowX, y=$windowY, placement=$windowPlacement}"
    }
}