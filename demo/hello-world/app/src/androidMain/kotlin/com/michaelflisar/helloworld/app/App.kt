package com.michaelflisar.helloworld.app

import androidx.compose.material3.LocalContentColor
import com.michaelflisar.helloworld.Shared
import com.michaelflisar.helloworld.core.Prefs
import com.michaelflisar.kotbilling.classes.Product
import com.michaelflisar.kotbilling.classes.ProductType
import com.michaelflisar.kotpreferences.core.value
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.ads.AdManagerImpl
import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.AppScope
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionAppDefaults
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.utils.AndroidAppIconUtil
import com.michaelflisar.toolbox.app.utils.createFileLogger
import com.michaelflisar.toolbox.backup.BackupDefaults
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.backup.BackupManagerImpl
import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupConfig
import com.michaelflisar.toolbox.backup.createDefaultBackupContent
import com.michaelflisar.toolbox.extensions.isLight
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.proversion.AndroidProVersionManager
import com.michaelflisar.toolbox.utils.AndroidUtil

class App : AndroidApplication() {

    override val appIcon: Int = R.mipmap.ic_launcher

    override fun onAfterCreate() {

        // 1) Init functions
        Shared.init(PlatformContext(this))
        val setup = AppSetup.get()

        // TODO HELLO WORLD
        // 2) Ads initialisieren (OPTIONAL)
        AdsManager.init(AdManagerImpl)

        // TODO HELLO WORLD
        // 3) ProVersionManager initialisieren (OPTIONAL)
        ProVersionManager.init(
            manager = AndroidProVersionManager(
                scope = AppScope,
                products = listOf(Product("pro", ProductType.InApp, false)),
                forceIsProInDebug = setup.debugPrefs.forceIsProInDebug,
                isDebug = BuildConfig.DEBUG,
                initialState = ProState.Unknown,
                log = true
            ),
            action = ProVersionAppDefaults.actionItem()
        )

        // TODO HELLO WORLD
        // 4) Backup Manager initialisieren (OPTIONAL)
        BackupManager.init(
            manager = BackupManagerImpl(
                config = BackupConfig(
                    backupContent = BackupDefaults.createDefaultBackupContent()
                ),
                autoBackupConfig = AutoBackupConfig(
                    appName = setup.name,
                    frequencyData = { setup.prefs.autoBackupFrequency.value },
                    backupPathData = { setup.prefs.backupPathData.value },
                )
            )
        )
    }

    override fun createSetup(): AppSetup {

        // 1) ) Storages erstellen
        val storageSettings = DataStoreStorage.create(name = "settings")
        val storageDebug = DataStoreStorage.create(name = "debug")

        // 2) Setups erstellen
        val setup = Shared.createBaseAppSetup(
            prefs = Prefs(storageSettings),
            debugPrefs = DebugPrefs(storageDebug),
            isDebugBuild = BuildConfig.DEBUG,
            fileLogger = AndroidUtil.createFileLogger(),
            icon = {
                AndroidAppIconUtil.adaptiveIconPainterResource(appIcon)
                    ?: Shared.appIcon(LocalContentColor.current.isLight())
            }
        )

        return setup
    }

    override fun createAndroidSetup() = AndroidAppSetup(
        buildConfigClass = BuildConfig::class
    )

}