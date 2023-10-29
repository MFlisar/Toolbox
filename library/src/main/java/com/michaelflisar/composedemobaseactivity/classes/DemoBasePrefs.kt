package com.michaelflisar.composedemobaseactivity.classes

import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.datastore.DataStoreStorage

object DemoBasePrefs : SettingsModel(DataStoreStorage(name = "demo_base_activity_theme_preferences")) {
    val theme by enumPref(DemoTheme.System)
    val dynamicTheme by boolPref(false)
}