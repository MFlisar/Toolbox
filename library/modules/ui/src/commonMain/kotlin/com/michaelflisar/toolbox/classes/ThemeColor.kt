package com.michaelflisar.toolbox.classes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.extensions.isDark

@Immutable
data class ThemeColor(
    val base: Color,
    val lightVariant: Color,
    val darkVariant: Color,
) {

    fun onColor(background: Color): Color =
        if (background.isDark()) darkVariant else lightVariant

    val onBackground: Color
        @Composable get() = onColor(MaterialTheme.colorScheme.background)

    val onPrimary: Color
        @Composable get() = onColor(MaterialTheme.colorScheme.primary)
}