package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledLocalMissingException

internal val LocalStyledThemeName = staticCompositionLocalOf<String> { throw StyledLocalMissingException("LocalStyledThemeName") }
