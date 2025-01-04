package com.michaelflisar.toolbox.classes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
class Style(
    // Minimum Component Size
    val minimumInteractiveComponentSize: Dp = 48.dp,
    // Paddings
    val paddingSmall: Dp = 4.dp,
    val paddingDefault: Dp = 8.dp,
    val paddingContent: Dp = 16.dp,
    // Spacing
    val spacingMini: Dp = 2.dp,
    val spacingSmall: Dp = 4.dp,
    val spacingDefault: Dp = 8.dp,
    // Scrollbar
    val scrollbar: Dp = 8.dp
) {
    companion object {
        fun windowsDefault() = Style(
            // Minimum Component Size
            minimumInteractiveComponentSize = 24.dp,
            // Paddings
            paddingSmall = 4.dp,
            paddingDefault = 8.dp,
            paddingContent = 8.dp,
            // Spacing
            spacingMini = 4.dp,
            spacingSmall = 4.dp,
            spacingDefault = 8.dp,
            // Scrollbar
            scrollbar = 8.dp
        )

        @Composable
        fun windowsTypography(): Typography {
            return MaterialTheme.typography
        }
    }
}

val LocalStyle = staticCompositionLocalOf { Style() }