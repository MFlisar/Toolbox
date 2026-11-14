package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.Platform

@Composable
actual fun Platform.UpdateComposeThemeStatusBar(
    statusBarColor: Color,
    navigationBarColor: Color,
    isDark: Boolean,
) {
    // no-op
}