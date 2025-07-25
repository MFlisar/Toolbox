package com.michaelflisar.toolbox.app.debug

import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.classes.BaseStorage

class DebugPrefs(storage: BaseStorage) : SettingsModel(storage) {

    val showDeveloperSettings by boolPref(false)
    val forceIsProInDebug by boolPref(false)
    val showDebugDrawer by boolPref(false)

    val showDebugOverlay by boolPref(true)
    val visualDebug by boolPref(false)
    val advancedLogs by boolPref(false)

    // DebugDrawer
    val debugDrawerExpandedIds by stringSetPref(emptySet())
    val showRegionInformations by boolPref(true)
    val showRegionDevice by boolPref(true)
    val showRegionThemes by boolPref(true)
    val showRegionLogging by boolPref(true)
    val showRegionData by boolPref(true)
}