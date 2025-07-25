package com.michaelflisar.toolbox.windows.prefs

import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.windows.jewel.JewelTheme

interface DesktopAppPrefs {

    val windowWidth: StorageSetting<Int>
    val windowHeight: StorageSetting<Int>
    val windowX: StorageSetting<Int>
    val windowY: StorageSetting<Int>
    val windowPlacement: StorageSetting<WindowPlacement>
    val alwaysOnTop: StorageSetting<Boolean>

    val jewelTheme: StorageSetting<JewelTheme>
    val contrast: StorageSetting<ComposeTheme.Contrast>
    val theme: StorageSetting<String>
}

