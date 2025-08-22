package com.michaelflisar.toolbox.app.features.preferences

import com.michaelflisar.kotpreferences.core.interfaces.Storage

actual fun Preferences.createStorage(name: String): Storage {
    // TODO
    throw NotImplementedError("DataStoreStorage is not implemented for iOS yet")

    // return DataStoreStorage.create(folder = IOSUtil.appDir(), name = name)
}