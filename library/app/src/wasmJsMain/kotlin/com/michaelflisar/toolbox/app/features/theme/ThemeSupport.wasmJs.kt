package com.michaelflisar.toolbox.app.features.theme

import com.michaelflisar.composestyled.classes.StyledThemeProvider
import com.michaelflisar.composestyled.fluent2.createFluent2ThemeProvider
import com.michaelflisar.composestyled.themes.fluent2.Fluent2Themes

actual fun themeProvider(): StyledThemeProvider = createFluent2ThemeProvider()

actual fun themeCollection() = Fluent2Themes.collection
