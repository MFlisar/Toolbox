package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.theme.scrollbar

@Stable
internal data class ScrollableData(
    val showScrollbar: Boolean,
    val paddingForScrollbar: Dp,
)

@Composable
internal fun rememberScrollableData(
    scrollState: ScrollableState,
    overlapScrollbar: Boolean,
): ScrollableData {
    val showScrollbar by remember {
        derivedStateOf {
            scrollState.canScrollForward || scrollState.canScrollBackward
        }
    }
    val padding =
        if (overlapScrollbar || !showScrollbar) 0.dp else MaterialTheme.scrollbar.paddingForScrollbar

    return remember(showScrollbar, padding) {
        ScrollableData(showScrollbar, padding)
    }
}