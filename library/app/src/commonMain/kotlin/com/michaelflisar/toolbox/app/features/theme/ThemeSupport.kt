package com.michaelflisar.toolbox.app.features.theme

import com.michaelflisar.composestyled.classes.StyledThemeProvider
import com.michaelflisar.composestyled.themes.StyledThemeColorsCollection

expect fun themeProvider(): StyledThemeProvider

expect fun themeCollection(): StyledThemeColorsCollection
