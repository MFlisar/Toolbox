package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.kotpreferences.core.classes.BaseStorage
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create

actual fun Preferences.createStorage(name: String): BaseStorage {
    // TODO
    throw NotImplementedError("DataStoreStorage is not implemented for iOS yet")

    // return DataStoreStorage.create(folder = IOSUtil.appDir(), name = name)
}