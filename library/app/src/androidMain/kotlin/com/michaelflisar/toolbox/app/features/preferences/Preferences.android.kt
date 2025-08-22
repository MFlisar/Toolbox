package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create

actual fun Preferences.createStorage(name: String): Storage {
    return DataStoreStorage.create(name = name)
}