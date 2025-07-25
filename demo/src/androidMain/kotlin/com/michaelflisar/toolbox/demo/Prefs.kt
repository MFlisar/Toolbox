package com.michaelflisar.toolbox.demo

import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ThemeDefault
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import com.michaelflisar.toolbox.app.platform.AppPrefs

object Prefs : SettingsModel(
    storage = DataStoreStorage.create(
        name = "settings"
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

    // Backup
    val backupPath by stringPref("")
}