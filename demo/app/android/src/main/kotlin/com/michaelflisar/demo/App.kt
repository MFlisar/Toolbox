package com.michaelflisar.demo

import androidx.compose.material3.LocalContentColor
import com.michaelflisar.demo.pages.tests.TestPrefs
import com.michaelflisar.kotpreferences.core.value
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.ads.AdManager
import com.michaelflisar.toolbox.ads.DEFAULT_IMPL
import com.michaelflisar.toolbox.app.AndroidApp
import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.AppScope
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
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
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_info
import com.michaelflisar.toolbox.demo.BuildConfig
import com.michaelflisar.toolbox.demo.R
import com.michaelflisar.toolbox.extensions.isLight
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.proversion.OpenIAPProVersionManager
import com.michaelflisar.toolbox.proversion.Product
import com.michaelflisar.toolbox.utils.AndroidUtil

class App : AndroidApplication() {

    override fun initEssential(): AppSetup {

        val appIcon = R.mipmap.ic_launcher

        // 1) Storages erstellen
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
        val androidSetup = AndroidAppSetup(
            buildConfigClass = BuildConfig::class
        )

        // 3) App initialisieren
        AndroidApp.init(
            app = this,
            setup = setup,
            androidSetup = androidSetup,
            appIcon = appIcon
        )

        return setup
    }

    override fun initFull(setup: AppSetup) {

        // 4) App Data ggf. updaten
        Shared.init(setup)

        // -----------------------------
        // 5) Sonstige Initialisierungen
        // -----------------------------

        // 1) test prefs
        val storageTest = DataStoreStorage.create(name = "test")
        App.registerSingleton(TestPrefs(storageTest))

        // 2) Ads + Pro Version enabled
        AdsManager.init(AdManager.DEFAULT_IMPL)

        ProVersionManager.init(
            manager = OpenIAPProVersionManager(
                appScope = AppScope,
                products = Product.ANDROID_TEST_PRODUCTS
                //listOf(Product("pro_version"))
                ,
                forceIsProInDebug = setup.debugPrefs.forceIsProInDebug,
                isDebug = BuildConfig.DEBUG,
                infoTextResource = Res.string.dlg_pro_version_info,
                initialState = ProState.Unknown,
                log = true
            ),
            action = { ProVersionAppDefaults.actionItem() }
        )

        // 3) Backup Support
        BackupManager.init(
            manager = BackupManagerImpl(
                config = BackupConfig(
                    backupContent = BackupDefaults.createDefaultBackupContent()
                ),
                autoBackupConfig = AutoBackupConfig(
                    appName = setup.appData.name,
                    frequencyData = { setup.prefs.autoBackupFrequency.value },
                    backupPathData = { setup.prefs.backupPathData.value },
                )
            )
        )
    }
}