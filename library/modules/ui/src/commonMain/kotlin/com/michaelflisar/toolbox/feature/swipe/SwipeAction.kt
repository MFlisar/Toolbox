package com.michaelflisar.toolbox.feature.swipe

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.logIf
import kotlin.math.abs

private val cardContentPaddingVertical = 8.dp
private val cardContentPaddingHorizontal = 8.dp

class SwipeAction(
    val onSwipe: (() -> Unit),
    val icon: ImageVector,
    val enabled: Boolean = true,
    val colorBackground: Color? = null,
    val colorForeground: Color? = null,
    val colorForegroundSuccess: Color? = null,
    val colorBackgroundSuccess: Color? = null
)

object SwipeActionDefaults {

    val cardContentPadding = PaddingValues(
        horizontal = cardContentPaddingHorizontal,
        vertical = cardContentPaddingVertical
    )

    @Composable
    fun dismissAction(
        onDelete: (() -> Unit),
        icon: ImageVector = Icons.Default.Delete,
        enabled: Boolean = true
    ) = SwipeAction(
        onSwipe = onDelete,
        icon = icon,
        enabled = enabled,
        colorBackground = null,
        colorForeground = MaterialTheme.colorScheme.error,
        colorBackgroundSuccess = MaterialTheme.colorScheme.errorContainer,
        colorForegroundSuccess = MaterialTheme.colorScheme.onErrorContainer
    )
}

@Composable
fun SwipeActionContent(
    modifier: Modifier = Modifier,
    swipeFromLeft: SwipeAction? = null,
    swipeFromRight: SwipeAction? = null,
    backgroundShape: Shape? = null,
    cardContentPadding: PaddingValues = SwipeActionDefaults.cardContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val directionFromLeft = SwipeToDismissBoxValue.StartToEnd
    val directionFromRight = SwipeToDismissBoxValue.EndToStart
    val threshold = 56.dp
    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    val confirmValueChange = { value: SwipeToDismissBoxValue ->
        if (value == directionFromLeft && swipeFromLeft != null) {
            L.logIf(ToolboxLogging.Tag.None)?.v { "onSwipeFromLeft..." }
            swipeFromLeft.onSwipe()
            false // State wird immer resettet
        } else if (value == directionFromRight && swipeFromRight != null) {
            L.logIf(ToolboxLogging.Tag.None)?.v { "onSwipeFromRight..." }
            swipeFromRight.onSwipe()
            false // State wird immer resettet
        } else false
    }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = confirmValueChange,
        positionalThreshold = positionalThreshold
    )

    SwipeToDismissBox(
        modifier = modifier,
        state = state,
        enableDismissFromEndToStart = swipeFromRight?.enabled == true,
        enableDismissFromStartToEnd = swipeFromLeft?.enabled == true,
        backgroundContent = {
            if (swipeFromLeft != null) {
                CreateRowSwipeBackground(
                    directionFromLeft,
                    state,
                    threshold,
                    swipeFromLeft,
                    backgroundShape,
                    cardContentPadding
                )
            }
            if (swipeFromRight != null) {
                CreateRowSwipeBackground(
                    directionFromRight,
                    state,
                    threshold,
                    swipeFromRight,
                    backgroundShape,
                    cardContentPadding
                )
            }
        }) {
        content()
    }
}

@Composable
private fun RowScope.CreateRowSwipeBackground(
    direction: SwipeToDismissBoxValue,
    dismissState: SwipeToDismissBoxState,
    threshold: Dp,
    swipeAction: SwipeAction,
    backgroundShape: Shape?,
    cardContentPadding: PaddingValues
) {
    // nur zeichnen, falls swipe im Gange ist...
    if (dismissState.dismissDirection != direction)
        return

    // TODO:
    // ggf. verbessern
    // https://stackoverflow.com/questions/78061022/swipetodismissbox-detect-if-releasing-would-result-in-a-dismiss-event
    val minOffset = with(LocalDensity.current) { threshold.toPx() }
    val success = dismissState.progress != 0f && abs(dismissState.requireOffset()) > minOffset

    //val success = dismissState.targetValue == direction

    val color by animateColorAsState(
        if (success) (swipeAction.colorBackgroundSuccess ?: swipeAction.colorBackground
        ?: MaterialTheme.colorScheme.background) else (swipeAction.colorBackground
            ?: Color.Transparent),
        label = ""
    )
    val iconColor by animateColorAsState(
        if (success) (swipeAction.colorForegroundSuccess ?: swipeAction.colorForeground
        ?: MaterialTheme.colorScheme.onBackground) else (swipeAction.colorForeground
            ?: MaterialTheme.colorScheme.onBackground),
        label = ""
    )
    val alignment = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    val scale by animateFloatAsState(
        if (success) 0.75f else 1f, label = ""
    )
    val alpha by animateFloatAsState(
        if (success) 0.5f else 1f, label = ""
    )

    Box(
        Modifier
            .fillMaxSize()
            .clip(
                backgroundShape ?: MaterialTheme.shapes.medium
            )// Shape wie die OutlineCard die als Content genutzt wird
            .background(color)
            .padding(cardContentPadding),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = swipeAction.icon,
            contentDescription = "",
            modifier = Modifier
                .size(24.dp)
                .scale(scale)
                .alpha(alpha),
            tint = iconColor
        )
    }
}