package com.michaelflisar.toolbox.app.features.theme

import com.michaelflisar.composestyled.classes.StyledThemeProvider
import com.michaelflisar.composestyled.material3.createMaterial3ThemeProvider
import com.michaelflisar.composestyled.themes.material3.Material3Themes

actual fun themeProvider(): StyledThemeProvider = createMaterial3ThemeProvider()

actual fun themeCollection() = Material3Themes.collection
