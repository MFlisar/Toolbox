package com.michaelflisar.demo

import com.michaelflisar.composestyled.isLight
import com.michaelflisar.composestyled.tokens.LocalStyledContentColor
import com.michaelflisar.kotpreferences.core.value
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.toolbox.ads.AdManager
import com.michaelflisar.toolbox.ads.DEFAULT_IMPL
import com.michaelflisar.toolbox.app.AppScope
import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionAppDefaults
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.init
import com.michaelflisar.toolbox.app.utils.AndroidAppIconUtil
import com.michaelflisar.toolbox.backup.BackupDefaults
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.backup.BackupManagerImpl
import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupMetadata
import com.michaelflisar.toolbox.backup.createDefaultBackupContent
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_info
import com.michaelflisar.toolbox.demo.BuildConfig
import com.michaelflisar.toolbox.demo.R
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.proversion.OpenIAPProVersionManager
import com.michaelflisar.toolbox.proversion.Product

class MainApp : AndroidApplication() {

    override fun initSetup() {

        val appIcon = R.mipmap.ic_launcher

        // 1) Storages erstellen
        val storageSettings = DataStoreStorage.create(name = "settings")
        val storageDebug = DataStoreStorage.create(name = "debug")

        // 2) Setups erstellen
        val setup = Shared.createSetup(
            storageSettings = storageSettings,
            storageDebug = storageDebug,
            fileLoggingSetup = FileLoggerSetup.Daily(),
            isDebugBuild = BuildConfig.DEBUG,
            icon = {
                AndroidAppIconUtil.adaptiveIconPainterResource(appIcon)
                    ?: Shared.appIcon(LocalStyledContentColor.current.isLight)
            }
        )
        val androidSetup = AndroidAppSetup(
            buildConfigClass = BuildConfig::class
        )

        // 3) App initialisieren
        App.init(this, setup, androidSetup, appIcon)
    }

    override fun initFull() {

        // 4) App Data ggf. updaten
        Shared.init()

        // 5) Ads
        AdsManager.init(AdManager.DEFAULT_IMPL)

        // 6) Pro Version
        ProVersionManager.init(
            manager = OpenIAPProVersionManager(
                appScope = AppScope,
                products = Product.ANDROID_TEST_PRODUCTS.take(1)
                //listOf(Product("pro_version"))
                ,
                forceIsProInDebug = AppSetup.get().debug.prefs.forceIsProInDebug,
                isDebug = BuildConfig.DEBUG,
                infoTextResource = Res.string.dlg_pro_version_info,
                initialState = ProState.Unknown,
                log = true
            ),
            menuItem = { ProVersionAppDefaults.menuItem(this) }
        )

        // 7) Backup Support
        BackupManager.init(
            manager = BackupManagerImpl(
                config = BackupConfig(
                    backupContent = BackupDefaults.createDefaultBackupContent(),
                    backupMetadata = BackupMetadata(
                        appId = AppSetup.get().appData.namespace,
                        appVersion = AppSetup.get().appData.versionName
                    ),
                    backupValidator = { metadata ->
                        true
                    }
                ),
                autoBackupConfig = AutoBackupConfig(
                    appName = AppSetup.get().appData.name,
                    frequencyData = { AppSetup.get().prefs.autoBackupFrequency.value },
                    backupPathData = { AppSetup.get().prefs.backupPathData.value },
                )
            )
        )
    }
}