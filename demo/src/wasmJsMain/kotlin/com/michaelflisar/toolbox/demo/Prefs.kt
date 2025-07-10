package com.michaelflisar.toolbox.demo

import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ThemeDefault
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.storage.keyvalue.LocalStorageKeyValueStorage
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import com.michaelflisar.toolbox.app.platform.AppPrefs

object Prefs : SettingsModel(
    storage = LocalStorageKeyValueStorage.create(
        key = "settings"
    )
), AppPrefs {

    // Theme
    override val customTheme by stringPref(ThemeDefault.Theme.id)
    override val theme by enumPref(
        ComposeTheme.BaseTheme.System,
        ComposeTheme.BaseTheme.entries
    )
    override val dynamicTheme by boolPref(false)
    override val contrast by enumPref(ComposeTheme.Contrast.System, ComposeTheme.Contrast.entries)
    override val toolbarStyle by enumPref(ToolbarStyle.Primary, ToolbarStyle.entries)

    // Changelog
    override val lastShownVersionForChangelog by longPref(-1L)
}