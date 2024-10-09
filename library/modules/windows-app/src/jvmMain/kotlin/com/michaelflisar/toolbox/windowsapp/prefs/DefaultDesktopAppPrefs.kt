package com.michaelflisar.toolbox.windowsapp.prefs

import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
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

}