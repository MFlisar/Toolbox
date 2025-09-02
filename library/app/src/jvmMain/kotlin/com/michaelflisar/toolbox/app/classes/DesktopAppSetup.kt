package com.michaelflisar.toolbox.app.classes

import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs

class DesktopAppSetup(
    val prefs: DesktopPrefs,
    val visible: Boolean = true,
    val resizable: Boolean = true,
    val enabled: Boolean = true,
    val focusable: Boolean = true,
    val swingCompatMode: Boolean = false
)