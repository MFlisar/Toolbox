package com.michaelflisar.toolbox.app.classes

import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs

class DesktopAppSetup(
    val prefs: DesktopPrefs,
    val ensureIsFullyOnScreen: Boolean = false,
    val rememberWindowState: Boolean = true,
    val minimumVisibleWidthPercentOnWindowRestore: Float = 1f,
    val minimumVisibleHeightPercentOnWindowRestore: Float = 1f,
) {
    companion object {
        private var instance: DesktopAppSetup? = null

        internal fun init(setup: DesktopAppSetup): Boolean {
            if (instance != null)
                return false

            instance = setup
            return true
        }

        fun get(): DesktopAppSetup =
            instance ?: error("DesktopAppSetup.init(setup) must be called before accessing it.")
    }
}
