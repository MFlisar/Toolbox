package com.michaelflisar.toolbox.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerButton
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerRegion
import com.michaelflisar.composethemer.FlatUIThemes
import com.michaelflisar.composethemer.Material500Themes
import com.michaelflisar.composethemer.MetroThemes
import com.michaelflisar.composethemer.themes.DefaultThemes
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.Constants
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.backup.IBackupSupport
import com.michaelflisar.toolbox.app.features.debugdrawer.DebugDrawer
import com.michaelflisar.toolbox.app.features.navigation.INavigationDefinition
import com.michaelflisar.toolbox.app.features.navigation.screen.INavScreen
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.app.features.update.UpdateManager
import com.michaelflisar.toolbox.app.platform.fileLogger
import com.michaelflisar.toolbox.demo.pages.PageHomeScreen
import com.michaelflisar.toolbox.demo.pages.PageSettingsScreen
import com.michaelflisar.toolbox.demo.pages.PageStatesScreen
import com.michaelflisar.toolbox.demo.pages.PageTestsRootScreenContainer
import com.michaelflisar.toolbox.demo.pages.tests.PageTestExpandableHeader
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.app_name
import com.michaelflisar.toolbox.demo.resources.mflisar
import com.michaelflisar.toolbox.extensions.toIconComposable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

object SharedDefinitions : INavigationDefinition {

    fun update(context: PlatformContext, setup: AppSetup = CommonApp.setup) {
        val updateManager = UpdateManager(
            listOf(
                //UpdateTo1,
            )
        )
        @OptIn(DelicateCoroutinesApi::class)
        updateManager.update(GlobalScope, context, setup)
    }

    @Composable
    fun appIcon(): Painter {
        return painterResource(Res.drawable.mflisar)
    }

    fun createBaseAppSetup(
        prefs: BasePrefs,
        debugStorage: Storage,
        icon: @Composable () -> Painter = { appIcon() },
        proVersionManager: BaseAppProVersionManager,
        backupSupport: IBackupSupport?,
        isDebugBuild: Boolean,
    ) = AppSetup(
        versionCode = BuildKonfig.versionCode,
        versionName = BuildKonfig.versionName,
        packageName = BuildKonfig.packageName,
        name = { stringResource(Res.string.app_name) },
        icon = icon,
        themeSupport = AppSetup.ThemeSupport.full(
            DefaultThemes.getAllThemes() +
                    FlatUIThemes.getAllThemes() +
                    MetroThemes.getAllThemes() +
                    Material500Themes.getAllThemes()
        ),
        prefs = prefs,
        debugPrefs = DebugPrefs(debugStorage),
        proVersionManager = proVersionManager,
        supportsChangelog = true,
        debugDrawer = { state ->
            DebugDrawer(state) {
                val appState = LocalAppState.current
                DebugDrawerRegion(
                    image = { Icon(Icons.Default.Info, null) },
                    label = "Import Worker",
                    drawerState = state
                ) {
                    DebugDrawerButton(label = "Test") {
                        appState.showToast("Test clicked")
                    }
                }
            }
        },
        privacyPolicyLink = "https://mflisar.github.io/android/flash-launcher/privacy-policy/",
        supportLanguagePicker = true,
        fileLogger = Platform.fileLogger,
        changelogSetup = ChangelogDefaults.setup(
            logFileReader = { Res.readBytes(Constants.CHANGELOG_PATH) },
            versionFormatter = Constants.CHANGELOG_FORMATTER
        ),
        backupSupport = backupSupport,
        isDebugBuild = isDebugBuild
    )

    // -------------------------
    // Pages
    // -------------------------

    val defaultPage = PageHomeScreen

    override val pagesMain: List<INavScreen> =
        listOf(PageHomeScreen, PageStatesScreen, PageTestsRootScreenContainer, PageTestExpandableHeader)
    override val pageSetting: INavScreen = PageSettingsScreen

    // -------------------------
    // Actions
    // -------------------------

    @Composable
    override fun actionCustom(): List<ActionItem.Action> = listOf(
        actionTest()
    )

    @Composable
    private fun actionTest(): ActionItem.Action {
        val appState = LocalAppState.current
        return ActionItem.Action(
            title = "TEST Action",
            icon = Icons.Default.ArrowRight.toIconComposable(),
            action = {
                appState.showSnackbar("Test clicked")
            }
        )
    }
}