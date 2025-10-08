package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.classes.DesktopWindowState
import com.michaelflisar.toolbox.app.jewel.JewelTheme

class DesktopPrefs(
    storage: Storage,
) : SettingsModel(storage) {

    // Window Einstellungen
    val windowState by anyStringPref(DesktopWindowState.CONVERTER, DesktopWindowState())
    val alwaysOnTop by boolPref(false)

    // jewel theme
    internal val jewelTheme by enumPref(JewelTheme.System, JewelTheme.entries)
}