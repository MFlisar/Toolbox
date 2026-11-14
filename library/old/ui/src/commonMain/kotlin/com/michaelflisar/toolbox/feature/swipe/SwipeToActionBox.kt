package com.michaelflisar.toolbox.feature.swipe

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalPointerSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.extensions.disabled
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private val cardContentPaddingVertical = 8.dp
private val cardContentPaddingHorizontal = 8.dp

class SwipeAction(
    val onSwipe: (() -> Unit),
    val icon: ImageVector,
    val enabled: Boolean = true,
    val finishBackAnimationBeforeCallback: Boolean = false,
    val colorBackground: Color? = null,
    val colorForeground: Color? = null,
    val colorForegroundSuccess: Color? = null,
    val colorBackgroundSuccess: Color? = null,
    val iconScale: Float = .75f,
    val iconScaleSuccess: Float = 1f,
) {
    enum class Direction {
        LeftToRight,
        RightToLeft,
        Settled
    }
}

object SwipeActionDefaults {

    val cardContentPadding = PaddingValues(
        horizontal = cardContentPaddingHorizontal,
        vertical = cardContentPaddingVertical
    )

    @Composable
    fun dismissAction(
        onDelete: (() -> Unit),
        icon: ImageVector = Icons.Default.Delete,
        enabled: Boolean = true,
    ) = SwipeAction(
        onSwipe = onDelete,
        icon = icon,
        enabled = enabled,
        colorBackground = null,
        colorForeground = MaterialTheme.colorScheme.error.disabled(),
        colorBackgroundSuccess = MaterialTheme.colorScheme.error,
        colorForegroundSuccess = MaterialTheme.colorScheme.onError
    )

    @Composable
    fun editAction(
        onRun: (() -> Unit),
        icon: ImageVector = Icons.Default.Edit,
        enabled: Boolean = true,
    ) = SwipeAction(
        onSwipe = onRun,
        icon = icon,
        enabled = enabled,
        colorBackground = null,
        colorForeground = MaterialTheme.colorScheme.onBackground.disabled(),
        colorBackgroundSuccess = MaterialTheme.colorScheme.onBackground,
        colorForegroundSuccess = MaterialTheme.colorScheme.background
    )
}

@Composable
fun SwipeToActionBox(
    modifier: Modifier = Modifier,
    swipeFromLeft: SwipeAction? = null,
    swipeFromRight: SwipeAction? = null,
    backgroundShape: Shape? = null,
    cardContentPadding: PaddingValues = SwipeActionDefaults.cardContentPadding,
    content: @Composable () -> Unit,
) {

    val swipeFromLeft by rememberUpdatedState(swipeFromLeft)
    val swipeFromRight by rememberUpdatedState(swipeFromRight)

    val threshold = 56.dp
    val thresholdInPx = with(LocalDensity.current) { threshold.toPx() }
    val scope = rememberCoroutineScope()
    var swiped by remember { mutableStateOf<SwipeAction.Direction?>(null) }
    val offsetX = remember { Animatable(0f) }
    LaunchedEffect(swiped) {
        offsetX.animateTo(0f, animationSpec = tween(300))
        when (swiped) {
            null,
            SwipeAction.Direction.Settled,
                -> {
                // do nothing
            }

            SwipeAction.Direction.LeftToRight -> {
                if (swipeFromLeft?.finishBackAnimationBeforeCallback == true) {
                    swipeFromLeft?.onSwipe()
                }
            }

            SwipeAction.Direction.RightToLeft -> {
                if (swipeFromRight?.finishBackAnimationBeforeCallback == true) {
                    swipeFromRight?.onSwipe()
                }
            }
        }
        swiped = null
    }

    val size = remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .onSizeChanged { size.value = it }
            .pointerInput(Unit) {
                val onDragStart = { _: Offset -> }
                val onDragEnd = {
                    when {
                        offsetX.value > thresholdInPx -> {
                            swiped = SwipeAction.Direction.LeftToRight
                            if (swipeFromLeft?.finishBackAnimationBeforeCallback == false) {
                                swipeFromLeft?.onSwipe()
                            }
                        }

                        offsetX.value < -thresholdInPx -> {
                            swiped = SwipeAction.Direction.RightToLeft
                            if (swipeFromRight?.finishBackAnimationBeforeCallback == false) {
                                swipeFromRight?.onSwipe()
                            }
                        }

                        else -> {
                            swiped = SwipeAction.Direction.Settled
                        }
                    }
                }
                val onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                    if (swipeFromLeft?.enabled == true && swipeFromRight?.enabled == true) {
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                        true
                    } else if (swipeFromLeft?.enabled == true) {
                        val newValue = (offsetX.value + dragAmount)
                            .coerceAtLeast(0f)
                            .coerceAtMost(size.value.width.toFloat())
                        if (newValue != offsetX.value) {
                            scope.launch { offsetX.snapTo(newValue) }
                            true
                        } else false
                    } else if (swipeFromRight?.enabled == true) {
                        val newValue = (offsetX.value + dragAmount)
                            .coerceAtMost(0f)
                            .coerceAtLeast(size.value.width.toFloat() * -1f)
                        if (newValue != offsetX.value) {
                            scope.launch { offsetX.snapTo(newValue) }
                            true
                        } else false
                    } else false
                }
                val onDragCancel = {}
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var overSlop = 0f
                    val drag = awaitHorizontalPointerSlopOrCancellation(
                        down.id,
                        down.type
                    ) { change, over ->
                        change.consume()
                        overSlop = over
                    }
                    if (drag != null) {
                        val direction = if (overSlop > 0)
                            SwipeAction.Direction.LeftToRight
                        else if (overSlop < 0)
                            SwipeAction.Direction.RightToLeft
                        else null

                        val actions = mapOf(
                            SwipeAction.Direction.LeftToRight to swipeFromLeft,
                            SwipeAction.Direction.RightToLeft to swipeFromRight
                        )

                        if (direction == null || actions[direction]?.enabled != true) {
                            onDragCancel()
                        } else {
                            onDragStart(drag.position)
                            onHorizontalDrag(drag, overSlop)
                            if (horizontalDrag(drag.id) {
                                    if (onHorizontalDrag(it, it.positionChange().x))
                                        it.consume()
                                }
                            ) {
                                onDragEnd()
                            } else {
                                onDragCancel()
                            }
                        }
                    }
                }

                // diese funktion consumed den drag immer, daher die manuelle Variante oben
                // detectHorizontalDragGestures(
                //     onDragEnd = onDragEnd,
                //     onHorizontalDrag = { change, dragAmount ->
                //         onHorizontalDrag(change, dragAmount)
                //     }
                // )
            }
    ) {
        // Background
        Row(modifier = Modifier.matchParentSize()) {
            val direction = if (offsetX.value > 0) SwipeAction.Direction.LeftToRight else if (offsetX.value < 0) SwipeAction.Direction.RightToLeft else SwipeAction.Direction.Settled
            if (swipeFromLeft?.enabled == true && direction == SwipeAction.Direction.LeftToRight) {
                CreateRowSwipeBackground(
                    direction = direction,
                    offsetInPx = offsetX.value.absoluteValue,
                    thresholdInPx = thresholdInPx,
                    swipeAction = swipeFromLeft!!,
                    backgroundShape = backgroundShape,
                    cardContentPadding = cardContentPadding
                )
            }
            if (swipeFromRight?.enabled == true && direction == SwipeAction.Direction.RightToLeft) {
                CreateRowSwipeBackground(
                    direction = direction,
                    offsetInPx = offsetX.value.absoluteValue,
                    thresholdInPx = thresholdInPx,
                    swipeAction = swipeFromRight!!,
                    backgroundShape = backgroundShape,
                    cardContentPadding = cardContentPadding
                )
            }
        }

        // Foreground
        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
        ) {
            content()
        }
    }
}

@Composable
private fun CreateRowSwipeBackground(
    direction: SwipeAction.Direction,
    offsetInPx: Float,
    thresholdInPx: Float,
    swipeAction: SwipeAction,
    backgroundShape: Shape?,
    cardContentPadding: PaddingValues,
) {
    val success = offsetInPx >= thresholdInPx

    val backgroundColor = swipeAction.colorBackground ?: MaterialTheme.colorScheme.background
    val backgroundColorSuccess = swipeAction.colorBackgroundSuccess ?: backgroundColor
    val iconColor = swipeAction.colorForeground ?: MaterialTheme.colorScheme.onBackground
    val iconColorSuccess = swipeAction.colorForegroundSuccess ?: iconColor

    val color by animateColorAsState(
        if (success) backgroundColorSuccess else backgroundColor,
        label = ""
    )
    val colorIcon by animateColorAsState(if (success) iconColorSuccess else iconColor, label = "")
    val alignment = when (direction) {
        SwipeAction.Direction.LeftToRight -> Alignment.CenterStart
        SwipeAction.Direction.RightToLeft -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    val scale by animateFloatAsState(
        if (success) swipeAction.iconScaleSuccess else swipeAction.iconScale, label = ""
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
                .scale(scale),
            tint = colorIcon
        )
    }
}