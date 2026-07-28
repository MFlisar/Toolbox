package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.michaelflisar.kotpreferences.core.SettingsConverter
import com.michaelflisar.kotpreferences.core.value
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
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.nio.file.AccessDeniedException
import kotlin.math.min
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
}

fun WindowState.resetWindowSize() {
    val prefs = DesktopAppSetup.get().prefs
    this.size = DpSize(
        prefs.windowState.defaultValue.windowWidth.dp,
        prefs.windowState.defaultValue.windowHeight.dp,
    )
}

fun WindowState.resetWindowPosition(density: Density, window: ComposeWindow) = reset(
    density = density,
    window = window,
    placement = false,
    size = false,
    position = true
)

private val screenBounds: List<Rectangle>
    get() = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .screenDevices
        .map { it.defaultConfiguration.bounds }

@OptIn(FlowPreview::class)
@Composable
fun rememberDesktopWindowState(
    prefs: BaseDesktopPrefs,
): WindowState {

    val desktopSetup = DesktopAppSetup.get()
    if (!desktopSetup.rememberWindowState) {
        return remember {
            getDefaultWindowState()
        }
    }

    val state = remember {
        // reads blocking, but we need this here instantly!
        val restored = prefs.windowState.value
        if (restored.isWindowPositionVisible(
                desktopSetup.minimumVisibleWidthPercentOnWindowRestore,
                desktopSetup.minimumVisibleHeightPercentOnWindowRestore
            )
        )
            restored.toWindowState()
        else
            getDefaultWindowState()
    }

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

fun getDefaultWindowState(): WindowState {

    val maxWidth = screenBounds.maxOfOrNull { it.width }
        ?: DesktopWindowState.DEFAULT_WIDTH

    val maxHeight = screenBounds.maxOfOrNull { it.height }
        ?: DesktopWindowState.DEFAULT_HEIGHT

    return WindowState(
        size = DpSize(
            min(
                DesktopWindowState.DEFAULT_WIDTH,
                maxWidth
            ).dp,
            min(
                DesktopWindowState.DEFAULT_HEIGHT,
                maxHeight
            ).dp
        ),
        position = WindowPosition.Aligned(Alignment.Center)
    )
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
        const val DEFAULT_WIDTH = 1024
        const val DEFAULT_HEIGHT = 800
    }

    constructor(windowState: WindowState) : this(
        windowWidth = windowState.size.width.value.toInt(),
        windowHeight = windowState.size.height.value.toInt(),
        windowX = windowState.position.x.value.toInt(),
        windowY = windowState.position.y.value.toInt(),
        windowPlacement = windowState.placement
    )

    fun isWindowPositionVisible(
        minVisibleWidthPercent: Float = 0.5f,
        minVisibleHeightPercent: Float = 0.5f,
    ): Boolean {

        if (windowPlacement == WindowPlacement.Maximized) {
            return true
        }

        if (windowWidth <= 0 || windowHeight <= 0) {
            return false
        }

        val windowBounds = Rectangle(
            windowX,
            windowY,
            windowWidth,
            windowHeight
        )

        val visibleWidth = screenBounds.sumOf { screen ->
            maxOf(
                0,
                screen.intersection(windowBounds).width
            )
        }

        val visibleHeight = screenBounds.sumOf { screen ->
            maxOf(
                0,
                screen.intersection(windowBounds).height
            )
        }

        val visibleWidthRatio =
            visibleWidth.toFloat() / windowWidth

        val visibleHeightRatio =
            visibleHeight.toFloat() / windowHeight

        return visibleWidthRatio >= minVisibleWidthPercent &&
                visibleHeightRatio >= minVisibleHeightPercent
    }

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