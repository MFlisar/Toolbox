package com.michaelflisar.toolbox.windowsapp.prefs

import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.classes.Theme

interface DesktopAppPrefs {

    val windowWidth: StorageSetting<Int>
    val windowHeight: StorageSetting<Int>
    val windowX: StorageSetting<Int>
    val windowY: StorageSetting<Int>
    val windowPlacement: StorageSetting<WindowPlacement>

    val theme: StorageSetting<Theme>
}

