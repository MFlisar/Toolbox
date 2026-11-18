package com.michaelflisar.toolbox.app.features.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private val debugInfoTextStyle = TextStyle(color = Color.White, fontSize = 12.sp)

const val DebugOverlayDescription = "Show a draggable overlay with memory usage and optional FPS"

@Composable
fun BoxScope.DebugOverlay(
    modifier: Modifier = Modifier,
    snapToEdge: Boolean = true,
    overlayBackground: Color = Color.Black.copy(alpha = 0.6f),
    overlayShape: Shape = RoundedCornerShape(4.dp),
) {
    if (!isEnabled()) return

    val prefs = AppSetup.get().debug.prefs
    val showDebugOverlay by prefs.showDebugOverlay.collectAsStateNotNull()
    if (!showDebugOverlay) return
    val showFps by prefs.showFps.collectAsStateNotNull()

    val positionX = prefs.debugOverlayPositionX.asMutableStateNotNull()
    val positionY = prefs.debugOverlayPositionY.asMutableStateNotNull()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val safeLeft = WindowInsets.safeDrawing.getLeft(density, layoutDirection)
    val safeTop = WindowInsets.safeDrawing.getTop(density)
    val safeRight = WindowInsets.safeDrawing.getRight(density, layoutDirection)
    val safeBottom = WindowInsets.safeDrawing.getBottom(density)
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    var overlaySize by remember { mutableStateOf(IntSize.Zero) }
    var memoryUsage by remember { mutableStateOf<Long?>(null) }
    var fps by remember { mutableStateOf(0) }
    var dragPosition by remember(positionX.value, positionY.value) {
        mutableStateOf(Offset(positionX.value, positionY.value))
    }
    val latestParentSize by rememberUpdatedState(parentSize)
    val latestOverlaySize by rememberUpdatedState(overlaySize)
    val latestSafeLeft by rememberUpdatedState(safeLeft)
    val latestSafeTop by rememberUpdatedState(safeTop)
    val latestSafeRight by rememberUpdatedState(safeRight)
    val latestSafeBottom by rememberUpdatedState(safeBottom)
    val latestSnapToEdge by rememberUpdatedState(snapToEdge)
    val draggableState = rememberDraggable2DState { dragAmount ->
        val maxX =
            (latestParentSize.width - latestSafeLeft - latestSafeRight - latestOverlaySize.width)
                .coerceAtLeast(0)
        val maxY =
            (latestParentSize.height - latestSafeTop - latestSafeBottom - latestOverlaySize.height)
                .coerceAtLeast(0)
        dragPosition = Offset(
            x = if (maxX == 0) dragPosition.x else
                (dragPosition.x + dragAmount.x / maxX).coerceIn(0f, 1f),
            y = if (maxY == 0) dragPosition.y else
                (dragPosition.y + dragAmount.y / maxY).coerceIn(0f, 1f),
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            memoryUsage = processMemoryUsageBytes()
            delay(1_000.milliseconds)
        }
    }
    LaunchedEffect(showFps) {
        if (!showFps) {
            fps = 0
            return@LaunchedEffect
        }

        var windowStart = 0L
        var frameCount = 0
        while (true) {
            val frameTime = withFrameNanos { it }
            if (windowStart == 0L) {
                windowStart = frameTime
                continue
            }
            frameCount++
            val elapsed = frameTime - windowStart
            if (elapsed >= 1_000_000_000L) {
                fps = (frameCount * 1_000_000_000L / elapsed).toInt()
                frameCount = 0
                windowStart = frameTime
            }
        }
    }

    val infoRows = listOfNotNull(
        DebugInfo("RAM", memoryUsage?.let { "${it / (1024L * 1024L)} MB" } ?: "N/A"),
        DebugInfo("FPS", "$fps").takeIf { showFps }
    )

    Column(
        modifier = modifier
            .offset {
                val maxX =
                    (parentSize.width - safeLeft - safeRight - overlaySize.width).coerceAtLeast(0)
                val maxY =
                    (parentSize.height - safeTop - safeBottom - overlaySize.height).coerceAtLeast(0)
                IntOffset(
                    x = safeLeft + (maxX * dragPosition.x).roundToInt(),
                    y = safeTop + (maxY * dragPosition.y).roundToInt(),
                )
            }
            .onGloballyPositioned { coordinates ->
                val newParentSize = coordinates.parentLayoutCoordinates?.size ?: IntSize.Zero
                if (newParentSize != parentSize) parentSize = newParentSize
                if (coordinates.size != overlaySize) overlaySize = coordinates.size
            }
            .draggable2D(
                state = draggableState,
                onDragStopped = {
                    val maxX =
                        (latestParentSize.width - latestSafeLeft - latestSafeRight - latestOverlaySize.width)
                            .coerceAtLeast(0)
                    val finalPosition = if (latestSnapToEdge && maxX > 0) {
                        dragPosition.copy(x = if (dragPosition.x <= 0.5f) 0f else 1f)
                    } else {
                        dragPosition
                    }
                    dragPosition = finalPosition
                    positionX.value = finalPosition.x
                    positionY.value = finalPosition.y
                },
            )
            .background(overlayBackground, overlayShape)
            .padding(6.dp)
            .width(IntrinsicSize.Min),
    ) {
        DebugInfoColumn(infoRows)
    }
}

@Composable
private fun DebugInfoColumn(rows: List<DebugInfo>) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            rows.forEach {
                StyledText(
                    text = it.label,
                    style = debugInfoTextStyle,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
            rows.forEach {
                StyledText(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.data,
                    style = debugInfoTextStyle.copy(textAlign = TextAlign.End),
                )
            }
        }
    }
}

@Immutable
private class DebugInfo(
    val label: String,
    val data: String,
)