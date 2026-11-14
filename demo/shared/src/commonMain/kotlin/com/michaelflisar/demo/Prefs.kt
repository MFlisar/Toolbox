package com.michaelflisar.demo

import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.theme.themeProvider

class Prefs(storage: Storage) : BasePrefs(
    storage = storage,
    initialThemeColors = themeProvider().defaultThemeColors
) {
    val test by stringPref("test")
}