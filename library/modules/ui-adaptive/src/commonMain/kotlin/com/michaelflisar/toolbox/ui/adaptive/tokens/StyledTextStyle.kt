package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle

internal val LocalStyledTextStyle = compositionLocalOf<TextStyle> { error("No text style provided") }