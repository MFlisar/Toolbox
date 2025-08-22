package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.classes.JewelWindowState
import com.michaelflisar.toolbox.app.jewel.JewelTheme

class DesktopPrefs(
    storage: Storage,
) : SettingsModel(storage) {

    // Window Einstellungen
    val windowState by anyStringPref(JewelWindowState.CONVERTER, JewelWindowState())
    val alwaysOnTop by boolPref(false)

    // jewel theme
    val jewelTheme by enumPref(JewelTheme.System, JewelTheme.entries)
}