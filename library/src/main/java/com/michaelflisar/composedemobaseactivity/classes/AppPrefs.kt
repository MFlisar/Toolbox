package com.michaelflisar.composedemobaseactivity.classes

import com.michaelflisar.composedemobaseactivity.classes.DemoTheme
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.datastore.DataStoreStorage

object AppPrefs : SettingsModel(DataStoreStorage(name = "app_prefs")) {
    val theme by enumPref(DemoTheme.System)
    val dynamicTheme by boolPref(false)
}