package com.michaelflisar.toolbox.app.interfaces

import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle

interface IAppPrefs {

    // Theming
    val theme: StorageSetting<ComposeTheme.BaseTheme>
    val contrast: StorageSetting<ComposeTheme.Contrast>
    val dynamicTheme: StorageSetting<Boolean>
    val customTheme: StorageSetting<String>
    val toolbarStyle: StorageSetting<ToolbarStyle>

    suspend fun isDark(isSystemInDarkTheme: Boolean): Boolean {
        return when (theme.read()) {
            ComposeTheme.BaseTheme.Light -> false
            ComposeTheme.BaseTheme.Dark -> true
            ComposeTheme.BaseTheme.System -> isSystemInDarkTheme
        }
    }

    // Changelog
    val lastShownVersionForChangelog: StorageSetting<Long>
}