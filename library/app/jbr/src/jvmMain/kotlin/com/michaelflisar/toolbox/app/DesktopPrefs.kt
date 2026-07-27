package com.michaelflisar.toolbox.app

import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.features.preferences.BaseDesktopPrefs
import com.michaelflisar.toolbox.app.jewel.JewelTheme

class DesktopPrefs(storage: Storage) : BaseDesktopPrefs(storage) {

    companion object {
        internal fun get() = DesktopAppSetup.get().prefs as DesktopPrefs
    }

    val jewelTheme by enumPref(JewelTheme.System, JewelTheme.entries)
}