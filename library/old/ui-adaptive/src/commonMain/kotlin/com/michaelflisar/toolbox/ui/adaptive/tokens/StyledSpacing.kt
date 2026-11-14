package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledLocalMissingException

@Immutable
data class StyledSpacing(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
)

internal val LocalStyledSpacing =
    staticCompositionLocalOf<StyledSpacing> { throw StyledLocalMissingException("LocalStyledSpacing") }
