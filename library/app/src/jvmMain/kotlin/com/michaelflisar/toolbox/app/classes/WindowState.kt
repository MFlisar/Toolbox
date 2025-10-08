package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
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
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.utils.WindowUtil
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.logIf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.AccessDeniedException

suspend fun WindowState.resetAll(density: Density, window: ComposeWindow) {
    reset(density, window, true, true, true)
}

suspend fun WindowState.reset(
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

suspend fun WindowState.resetWindowSize() {
    val prefs = DesktopAppSetup.get().prefs
    this.size = DpSize(
        prefs.windowState.defaultValue.windowWidth.dp,
        prefs.windowState.defaultValue.windowHeight.dp,
    )
    //prefs.windowState.update(JewelWindowState(this))
}

suspend fun WindowState.resetWindowPosition(density: Density, window: ComposeWindow) = reset(
    density = density,
    window = window,
    placement = false,
    size = false,
    position = true
)

@OptIn(FlowPreview::class)
@Composable
fun rememberJewelWindowState(
    prefs: DesktopPrefs,
): WindowState {

    val scope = rememberCoroutineScope()

    val windowState by prefs.windowState.collectAsStateNotNull()
    val state = remember(windowState) {  windowState.toWindowState() }

    snapshotFlow { DesktopWindowState(state) }
        .distinctUntilChanged()
        .debounce(500)
        .onEach {
            L.logIf(ToolboxLogging.Tag.Window)?.i { "Saving window state: $it" }
            withContext(Dispatchers.IO) {
                try {
                    prefs.windowState.update(it)
                } catch (e: AccessDeniedException) {
                    // ignore - comes from androidx datastore...
                    L.logIf(ToolboxLogging.Tag.None)?.i(e)
                }
            }
        }
        .launchIn(scope)

    return state
}

@Serializable
data class DesktopWindowState(
    val windowWidth: Int = 1024,
    val windowHeight: Int = 800,
    val windowX: Int = 0,
    val windowY: Int = 0,
    val windowPlacement: WindowPlacement = WindowPlacement.Floating
) {
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