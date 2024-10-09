package com.michaelflisar.toolbox.windowsapp.prefs

import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object DesktopAppPrefs : SettingsModel(DataStoreStorage.create(
    folder = File(Paths.get("").absolutePathString()),
    name = "prefs"
)) {
    // Window Einstellungen
    val windowWidth by intPref(1024)
    val windowHeight by intPref(800)
    val windowX by intPref(0)
    val windowY by intPref(0)
    val windowPlacement by enumPref(WindowPlacement.Floating, WindowPlacement.entries)
}