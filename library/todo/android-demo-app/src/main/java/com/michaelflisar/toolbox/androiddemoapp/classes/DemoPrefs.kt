package com.michaelflisar.toolbox.androiddemoapp.classes

import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ThemeDefault
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.enumPref
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create

object DemoPrefs : SettingsModel(DataStoreStorage.create(name = "demo_prefs")) {
    val theme by stringPref(ThemeDefault.Theme.id)
    val baseTheme by enumPref(ComposeTheme.BaseTheme.System)
    val dynamic by boolPref(false)
    val contrast by enumPref(ComposeTheme.Contrast.System)
}