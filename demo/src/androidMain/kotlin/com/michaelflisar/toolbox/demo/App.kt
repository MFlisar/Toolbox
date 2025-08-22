package com.michaelflisar.toolbox.demo

import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.features.backup.AndroidBackupSupport
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManagerDisabled
import com.michaelflisar.toolbox.app.utils.AndroidAppIconUtil

class App : AndroidApplication() {

    override val appIcon: Int = R.mipmap.ic_launcher
    override val appName: Int = R.string.app_name

    override fun onCreate() {
        super.onCreate()
        SharedDefinitions.update(PlatformContext(this))
    }

    override fun createSetup(): AppSetup {
        val prefs = BasePrefs(Preferences.createStorage("settings"))
        return SharedDefinitions.createBaseAppSetup(
            prefs = prefs,
            debugStorage = Preferences.createStorage("debug"),
            icon = { AndroidAppIconUtil.adaptiveIconPainterResource(appIcon) ?: SharedDefinitions.appIcon() },
            proVersionManager = ProVersionManagerDisabled,
            backupSupport = AndroidBackupSupport(),
            isDebugBuild = BuildConfig.DEBUG
        )
    }

    override fun createAndroidSetup() = AndroidAppSetup(
        buildConfigClass = BuildConfig::class
    )

}