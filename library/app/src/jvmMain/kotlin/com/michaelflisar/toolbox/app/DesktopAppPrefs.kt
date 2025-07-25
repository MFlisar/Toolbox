package com.michaelflisar.toolbox.app

import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.app.interfaces.IAppPrefs
import com.michaelflisar.toolbox.app.jewel.JewelTheme

interface DesktopAppPrefs : IAppPrefs {

    // windows window settings
    val windowWidth: StorageSetting<Int>
    val windowHeight: StorageSetting<Int>
    val windowX: StorageSetting<Int>
    val windowY: StorageSetting<Int>
    val windowPlacement: StorageSetting<WindowPlacement>
    val alwaysOnTop: StorageSetting<Boolean>

    // jewel theme
    val jewelTheme: StorageSetting<JewelTheme>
}

