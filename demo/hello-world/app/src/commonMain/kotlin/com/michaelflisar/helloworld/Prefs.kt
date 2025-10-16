package com.michaelflisar.helloworld

import com.michaelflisar.helloworld.core.Prefs
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs

class Prefs(storage: Storage) : BasePrefs(storage) {

    companion object {
        fun get() = AppSetup.get().prefs as Prefs
    }
}