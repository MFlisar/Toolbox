package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.theme.ThemeSetup

expect val Platform.showToast: ((message: String, duration: Int) -> Unit)?
@Composable
expect fun Platform.UpdateComposeThemeStatusBar(
    statusBarColor: Color,
    navigationBarColor: Color,
    isDark: Boolean,
)

@Composable
fun isAppInDarkTheme() = ThemeSetup.get().isDarkTheme()