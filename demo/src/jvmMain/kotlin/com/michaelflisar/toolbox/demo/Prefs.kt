package com.michaelflisar.toolbox.demo

import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ThemeDefault
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import com.michaelflisar.toolbox.app.platform.AppPrefs
import com.michaelflisar.toolbox.app.jewel.JewelTheme
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object Prefs : SettingsModel(
    storage = DataStoreStorage.create(
        folder = File(Paths.get("").absolutePathString()),
        name = "settings"
    )
), AppPrefs {

    // Window Einstellungen
    override val windowWidth by intPref(1024)
    override val windowHeight by intPref(800)
    override val windowX by intPref(0)
    override val windowY by intPref(0)
    override val windowPlacement by enumPref(WindowPlacement.Floating, WindowPlacement.entries)
    override val alwaysOnTop by boolPref(false)

    // Theme
    override val jewelTheme by enumPref(JewelTheme.System, JewelTheme.entries)
    override val customTheme by stringPref(ThemeDefault.Theme.id)
    override val theme by enumPref(
        ComposeTheme.BaseTheme.System,
        ComposeTheme.BaseTheme.entries
    )
    override val contrast by enumPref(ComposeTheme.Contrast.System, ComposeTheme.Contrast.entries)
    override val dynamicTheme by boolPref(false) // not supported on desktop anyways...
    override val toolbarStyle by enumPref(ToolbarStyle.Primary, ToolbarStyle.entries)

    // Changelog
    override val lastShownVersionForChangelog by longPref(-1L)
}