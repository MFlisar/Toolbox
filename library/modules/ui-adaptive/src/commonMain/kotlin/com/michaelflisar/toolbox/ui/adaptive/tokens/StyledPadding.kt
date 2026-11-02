package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledLocalMissingException

@Immutable
data class StyledPadding(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val content: Dp,
    val cardContent: Dp
)

internal val LocalStyledPadding = staticCompositionLocalOf<StyledPadding> { throw StyledLocalMissingException("LocalStyledPadding") }
