package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.kotpreferences.storage.keyvalue.LocalStorageKeyValueStorage

actual fun Preferences.createStorage(name: String): Storage {
    return LocalStorageKeyValueStorage.create(key = name)
}