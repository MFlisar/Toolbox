package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider

/*
 * in a popup provide the offset like following:
 * DropdownMenu(
      modifier = modifier
          .onGloballyPositioned {
              offset.value = it.positionOnScreen() // use this offset for the tooltip
          }
 *  ) { ... }
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTooltipBox(
    tooltip: String,
    offset: IntOffset = IntOffset.Zero,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        if (enabled && tooltip.isNotEmpty()) {
            TooltipBox(
                modifier = Modifier,
                positionProvider = rememberMyTooltipBoxPositionProvider(offset),
                tooltip = {
                    PlainTooltip {
                        Text(tooltip)
                    }
                },
                state = rememberTooltipState()
            ) {
                content()
            }
        } else {
            content()
        }
    }
}

/*
 * in a popup provide the offset like following:
 * DropdownMenu(
      modifier = modifier
          .onGloballyPositioned {
              offset.value = it.positionOnScreen() // use this offset for the tooltip
          }
 *  ) { ... }
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTooltipBox(
    tooltip: @Composable () -> Unit,
    offset: IntOffset = IntOffset.Zero,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (enabled) {
        TooltipBox(
            modifier = modifier,
            positionProvider = rememberMyTooltipBoxPositionProvider(offset),
            tooltip = {
                PlainTooltip {
                    tooltip()
                }
            },
            state = rememberTooltipState()
        ) {
            content()
        }
    } else {
        Box(modifier = modifier) {
            content()
        }
    }
}

private val SpacingBetweenTooltipAndAnchor = 4.dp

/*
 * This is a custom position provider for the tooltip.
 * copied and adjust from TooltipDefaults.rememberPlainTooltipPositionProvider()
 *
 * it adjusts the position based on an offset
 * use offset zero for the default behaviour!
 *
 * useful to show tooltips inside a popup
 * Usage:
 * DropdownMenu(
      modifier = modifier
          .onGloballyPositioned {
              offset.value = it.positionOnScreen() // use this offset for the tooltip
          }
 *  ) { ... }
 *
 * param offset: the offset of the popup
 */
@Composable
fun rememberMyTooltipBoxPositionProvider(
    offset: IntOffset = IntOffset.Zero,
    spacingBetweenTooltipAndAnchor: Dp = SpacingBetweenTooltipAndAnchor,
): PopupPositionProvider {
    val tooltipAnchorSpacing = with(LocalDensity.current) {
        spacingBetweenTooltipAndAnchor.roundToPx()
    }
    return remember(tooltipAnchorSpacing, offset) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset {

                var anchorBounds = anchorBounds.translate(offset)

                val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2

                // Tooltip prefers to be above the anchor,
                // but if this causes the tooltip to overlap with the anchor
                // then we place it below the anchor
                var y = anchorBounds.top - popupContentSize.height - tooltipAnchorSpacing
                if (y < 0) y = anchorBounds.bottom + tooltipAnchorSpacing
                return IntOffset(x, y)

            }
        }
    }
}