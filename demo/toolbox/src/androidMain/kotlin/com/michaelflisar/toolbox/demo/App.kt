package com.michaelflisar.toolbox.demo

import com.michaelflisar.kotbilling.classes.Product
import com.michaelflisar.kotbilling.classes.ProductType
import com.michaelflisar.kotpreferences.core.value
import com.michaelflisar.toolbox.ads.AndroidAdManager
import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage
import com.michaelflisar.toolbox.app.features.proversion.ProVersionAppDefaults
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.utils.AndroidAppIconUtil
import com.michaelflisar.toolbox.backup.AndroidBackupDefaults
import com.michaelflisar.toolbox.backup.AndroidBackupManager
import com.michaelflisar.toolbox.backup.BackupConfig
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.proversion.AndroidProVersionManager
import kotlinx.coroutines.GlobalScope

class App : AndroidApplication() {

    override val appIcon: Int = R.mipmap.ic_launcher
    override val appName: Int = R.string.app_name

    override fun onAfterCreate() {
        SharedDefinitions.update(PlatformContext(this))

        // Ads + Pro Version enabled
        val setup = CommonApp.setup
        AdsManager.init(AndroidAdManager)
        ProVersionManager.init(
            manager = AndroidProVersionManager(
                scope = GlobalScope,
                products = listOf(Product("pro", ProductType.InApp, false)),
                forceIsProInDebug = setup.debugPrefs.forceIsProInDebug,
                isDebug = BuildConfig.DEBUG,
                initialState = ProState.Unknown,
                log = true
            ),
            action = ProVersionAppDefaults.actionItem()
        )

        // Backup Support
        BackupManager.init(
            manager = AndroidBackupManager(
                config = BackupConfig(
                    backupContent = AndroidBackupDefaults.createDefaultBackupContent()
                ),
                autoBackupConfig = AutoBackupConfig(
                    appName = getString(R.string.app_name),
                    frequencyData = { setup.prefs.autoBackupFrequency.value },
                    backupPathData = { setup.prefs.backupPathData.value },
                )
            )
        )
    }

    override fun createSetup(): AppSetup {
        val prefs = BasePrefs(Preferences.createStorage("settings"))
        return SharedDefinitions.createBaseAppSetup(
            prefs = prefs,
            debugStorage = Preferences.createStorage("debug"),
            icon = {
                AndroidAppIconUtil.adaptiveIconPainterResource(appIcon)
                    ?: SharedDefinitions.appIcon()
            },

            isDebugBuild = BuildConfig.DEBUG
        )
    }

    override fun createAndroidSetup() = AndroidAppSetup(
        buildConfigClass = BuildConfig::class
    )
}