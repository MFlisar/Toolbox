package com.michaelflisar.toolbox.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components

object MyDropdownMenuDefaults {
    val maxWidth: Dp = 280.dp
}

object MyDropdownMenu {

    sealed interface WidthConfig {

        /**
         * Wraps content up to [maxWidth]. If [limitToContainerWidth] is true,
         * the menu is also capped at the measured container width.
         */
        data class Auto(
            val maxWidth: Dp? = MyDropdownMenuDefaults.maxWidth,
            val limitToContainerWidth: Boolean = false,
        ) : WidthConfig

        /**
         * Matches the container width. If [maxWidth] is provided, it caps the
         * resulting menu width, including when the container is wider.
         */
        data class MatchParent(
            val maxWidth: Dp? = null,
        ) : WidthConfig
    }
}

class MyDropdownMenuContainerScope internal constructor(
    internal val containerWidth: IntState,
)

@Composable
fun MyDropdownMenuContainerScope.MyDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    widthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    shape: Shape = MaterialTheme.components.menu.shape,
    containerColor: Color = MenuDefaults.containerColor,
    tonalElevation: Dp = MenuDefaults.TonalElevation,
    shadowElevation: Dp = MenuDefaults.ShadowElevation,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {

    val widthPx = containerWidth.intValue
    if (widthPx <= 0) return

    val containerWidthDp = with(LocalDensity.current) { widthPx.toDp() }
    val menuWidthModifier = when (widthConfig) {
        is MyDropdownMenu.WidthConfig.Auto -> {
            val maxWidth = when {
                widthConfig.maxWidth != null && widthConfig.limitToContainerWidth ->
                    minOf(widthConfig.maxWidth, containerWidthDp)

                widthConfig.maxWidth != null -> widthConfig.maxWidth
                widthConfig.limitToContainerWidth -> containerWidthDp
                else -> null
            }
            maxWidth?.let { Modifier.widthIn(max = it) } ?: Modifier
        }

        is MyDropdownMenu.WidthConfig.MatchParent -> {
            if (widthConfig.maxWidth != null) {
                Modifier.widthIn(
                    min = minOf(containerWidthDp, widthConfig.maxWidth),
                    max = widthConfig.maxWidth,
                )
            } else {
                Modifier.widthIn(min = containerWidthDp)
            }
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = menuWidthModifier.then(modifier),
        offset = offset,
        scrollState = scrollState,
        shape = shape,
        containerColor = containerColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        content = content,
    )
}

@Composable
fun MyDropdownMenuContainer(
    modifier: Modifier = Modifier,
    content: @Composable MyDropdownMenuContainerScope.() -> Unit,
) {
    val containerWidth = remember { mutableIntStateOf(0) }
    val scope = remember(containerWidth) { MyDropdownMenuContainerScope(containerWidth) }

    Box(
        modifier = modifier.onSizeChanged { size ->
            if (containerWidth.intValue != size.width) {
                containerWidth.intValue = size.width
            }
        },
    ) {
        content(scope)
    }
}
