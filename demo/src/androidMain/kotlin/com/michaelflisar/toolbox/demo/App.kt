package com.michaelflisar.toolbox.demo

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.classes.AndroidAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.backup.AndroidBackupSupport
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage
import com.michaelflisar.toolbox.app.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManagerDisabled
import com.michaelflisar.toolbox.app.utils.AndroidAppIconUtil
import com.michaelflisar.toolbox.classes.ProState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
            proVersionManager = ProVersionManagerFixed(ProState.No),
            backupSupport = AndroidBackupSupport(),
            isDebugBuild = BuildConfig.DEBUG
        )
    }

    override fun createAndroidSetup() = AndroidAppSetup(
        buildConfigClass = BuildConfig::class
    )

    // TODO: nur für Testzwecke hier!
    class ProVersionManagerFixed(
        private val state: ProState
    ) : BaseAppProVersionManager {

        override val supportsProVersion = false

        private val _proState = MutableStateFlow(state)
        override val proState: StateFlow<ProState>
            get() = _proState

        override suspend fun checkProVersion(): ProState {
            return state
        }

        @Composable
        override fun actionProVersion(): ActionItem.Action? = null
    }

}