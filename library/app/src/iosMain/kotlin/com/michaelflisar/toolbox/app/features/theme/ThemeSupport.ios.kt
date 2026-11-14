package com.michaelflisar.toolbox.app.features.theme

import com.michaelflisar.composestyled.classes.StyledThemeProvider
import com.michaelflisar.composestyled.cupertino.flat.createCupertinoFlatThemeProvider
import com.michaelflisar.composestyled.themes.cupertino.CupertinoThemes

actual fun themeProvider(): StyledThemeProvider = createCupertinoFlatThemeProvider()

actual fun themeCollection() = CupertinoThemes.collection
