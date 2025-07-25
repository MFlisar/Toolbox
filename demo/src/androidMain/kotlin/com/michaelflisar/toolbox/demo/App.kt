package com.michaelflisar.toolbox.demo

import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.app.features.backup.AndroidBackupSupport
import com.michaelflisar.toolbox.app.utils.AndroidAppIconUtil

class App : AndroidApplication() {

    override val appIcon: Int = R.mipmap.ic_launcher
    override val appName: Int = R.string.app_name

    override fun createSetup(): AppSetup {
        return Shared.createBaseAppSetup(
            prefs = Prefs,
            debugStorage = DataStoreStorage.create(name = "debug"),
            icon = { AndroidAppIconUtil.adaptiveIconPainterResource(appIcon) ?: Shared.appIcon() },
            backupSupport = AndroidBackupSupport(
                prefBackupPath = Prefs.backupPath
            ),
            isDebugBuild = BuildConfig.DEBUG
        )
    }

    override fun createAndroidSetup() = AndroidAppSetup(
        buildConfigClass = BuildConfig::class
    )

}