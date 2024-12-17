package com.michaelflisar.toolbox.windows.prefs

import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.classes.Theme
import com.michaelflisar.toolbox.windows.jewel.JewelTheme
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object DefaultDesktopAppPrefs : SettingsModel(
    DataStoreStorage.create(
    folder = File(Paths.get("").absolutePathString()),
    name = "window"
)), DesktopAppPrefs {

    // Window Einstellungen
    override val windowWidth by intPref(1024)
    override val windowHeight by intPref(800)
    override val windowX by intPref(0)
    override val windowY by intPref(0)
    override val windowPlacement by enumPref(WindowPlacement.Floating, WindowPlacement.entries)
    override val alwaysOnTop by boolPref(false)

    // DesktopApp
    override val theme by enumPref(Theme.System, Theme.entries)

    override val jewelTheme by enumPref(JewelTheme.System, JewelTheme.entries)

}