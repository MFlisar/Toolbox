package com.michaelflisar.toolbox.demo

import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.utils.AndroidAppIconUtil
import com.michaelflisar.toolbox.app.AndroidApplication

class App : AndroidApplication() {

    override val appIcon: Int = R.mipmap.ic_launcher
    override val appName: Int = R.string.app_name

    override fun createSetup(): AppSetup {
        return Shared.createBaseAppSetup(
            prefs = Prefs,
            debugStorage = DataStoreStorage.create(name = "debug"),
            icon = { AndroidAppIconUtil.adaptiveIconPainterResource(appIcon) ?: Shared.appIcon() }
        )
    }

}