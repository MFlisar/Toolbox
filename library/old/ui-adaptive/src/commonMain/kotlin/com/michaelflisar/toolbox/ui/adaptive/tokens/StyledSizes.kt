package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledLocalMissingException

@Immutable
data class StyledSizes(
    // Interactive Sizes
    val minimumInteractiveSize: Dp,
    val minimumInteractiveSmallSize: Dp,
    // Icon Size
    val iconSize: Dp,
)

@Composable
fun Modifier.minimumInteractiveSize(small: Boolean = false) = this.then(
    Modifier
        .sizeIn(
            minWidth = if (small) LocalStyledSizes.current.minimumInteractiveSmallSize else LocalStyledSizes.current.minimumInteractiveSize,
            minHeight = if (small) LocalStyledSizes.current.minimumInteractiveSmallSize else LocalStyledSizes.current.minimumInteractiveSize
        )
)

internal val LocalStyledSizes = staticCompositionLocalOf<StyledSizes> {
    throw StyledLocalMissingException(
        "LocalStyledSizes"
    )
}
