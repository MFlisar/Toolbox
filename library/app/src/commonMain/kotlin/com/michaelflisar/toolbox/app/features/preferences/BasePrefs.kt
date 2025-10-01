package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ThemeDefault
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

open class BasePrefs(
    storage: Storage,
    initialTheme: ComposeTheme.Theme = ThemeDefault.Theme,
    initialFrequency: Frequency = Frequency.Weekly(DayOfWeek.SUNDAY, LocalTime(22, 0), 1)
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
    val backupPathData by stringPref("")
    val autoBackupFrequency by stringPref(initialFrequency.serialize())
}