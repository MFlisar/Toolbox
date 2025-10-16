package com.michaelflisar.toolbox.app.features.stickyheaders

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Stable
class StickyHeaderState {
    val headerPositions = mutableStateListOf<Pair<String, Float>>() // id, y
    var scrollY by mutableFloatStateOf(0f)
}

@Composable
fun rememberStickyHeaderState(): StickyHeaderState = remember { StickyHeaderState() }

@OptIn(ExperimentalUuidApi::class)
fun Modifier.stickyHeader(
    state: StickyHeaderState,
    scrollState: ScrollState
): Modifier = composed {
    val headerId = remember { Uuid.random().toString() }
    var yOffset by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    DisposableEffect(headerId) {
        onDispose {
            state.headerPositions.removeAll { it.first == headerId }
        }
    }

    var isSticky by remember { mutableStateOf(false) }
    var offsetPx by remember { mutableFloatStateOf(0f) }

    this
        .onGloballyPositioned { coordinates ->
            yOffset = coordinates.positionInParent().y
            val pos = state.headerPositions.indexOfFirst { it.first == headerId }
            if (pos >= 0) state.headerPositions[pos] = headerId to yOffset
            else state.headerPositions.add(headerId to yOffset)
        }
        .drawWithContent {
            state.scrollY = scrollState.value.toFloat()
            val sorted = state.headerPositions.sortedBy { it.second }
            val myIndex = sorted.indexOfFirst { it.first == headerId }
            val stickyId = sorted.lastOrNull { it.second <= state.scrollY }?.first
            isSticky = stickyId == headerId

            // Push-off Logik
            var offset = 0f
            if (isSticky) {
                offset = state.scrollY - yOffset
                // Prüfe, ob nächster Header kommt
                if (myIndex + 1 < sorted.size) {
                    val nextHeaderY = sorted[myIndex + 1].second
                    val headerHeight = size.height
                    val maxOffset = nextHeaderY - yOffset - headerHeight
                    if (offset > maxOffset) {
                        offset = maxOffset
                    }
                }
            }
            offsetPx = offset

            with(density) {
                drawContext.canvas.save()
                drawContext.canvas.translate(0f, offsetPx)
                this@drawWithContent.drawContent()
                drawContext.canvas.restore()
            }
        }
        .zIndex(if (isSticky) 1f else 0f)
}