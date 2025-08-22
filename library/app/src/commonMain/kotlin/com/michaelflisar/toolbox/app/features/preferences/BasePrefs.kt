package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ThemeDefault
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle

open class BasePrefs(
    storage: Storage,
    initialTheme: ThemeDefault.Theme = ThemeDefault.Theme
) : SettingsModel(storage) {

    // Theme
    val customTheme by stringPref(initialTheme.id)
    val theme by enumPref(
        ComposeTheme.BaseTheme.System,
        ComposeTheme.BaseTheme.entries
    )
    val dynamicTheme by boolPref(false)
    val contrast by enumPref(ComposeTheme.Contrast.System, ComposeTheme.Contrast.entries)
    val toolbarStyle by enumPref(ToolbarStyle.Primary, ToolbarStyle.entries)

    suspend fun isDark(isSystemInDarkTheme: Boolean): Boolean {
        return when (theme.read()) {
            ComposeTheme.BaseTheme.Light -> false
            ComposeTheme.BaseTheme.Dark -> true
            ComposeTheme.BaseTheme.System -> isSystemInDarkTheme
        }
    }

    // Changelog
    val lastShownVersionForChangelog by longPref(-1L)

    // Version
    val lastAppVersion by longPref(-1L)

    // Backup
    val backupPath by stringPref("")
}