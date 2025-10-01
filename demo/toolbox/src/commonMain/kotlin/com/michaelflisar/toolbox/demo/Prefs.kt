package com.michaelflisar.toolbox.demo

import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs

class Prefs(storage: Storage) : BasePrefs(storage) {
    val test by stringPref("test")
}