package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.interfaces.Storage

open class BaseDebugPrefs(storage: Storage) : SettingsModel(storage) {
    val showDeveloperSettings by boolPref(false)
    val forceIsProInDebug by boolPref(false)
    val showDebugDrawer by boolPref(false)
    val showDebugOverlay by boolPref(false)
    val showFps by boolPref(true)
    val debugOverlayPositionX by floatPref(1f)
    val debugOverlayPositionY by floatPref(0f)
}