package com.michaelflisar.helloworld

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composethemer.FlatUIThemes
import com.michaelflisar.composethemer.Material500Themes
import com.michaelflisar.composethemer.MetroThemes
import com.michaelflisar.composethemer.themes.DefaultThemes
import com.michaelflisar.helloworld.app.BuildKonfig
import com.michaelflisar.helloworld.core.resources.Res
import com.michaelflisar.helloworld.core.resources.app_name
import com.michaelflisar.helloworld.core.resources.mflisar
import com.michaelflisar.helloworld.feature.page1.ScreenPage1
import com.michaelflisar.helloworld.feature.page2.ScreenPage2
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.Constants
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.DebugDrawer
import com.michaelflisar.toolbox.app.features.navigation.INavigationDefinition
import com.michaelflisar.toolbox.app.features.navigation.screen.INavScreen
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.update.UpdateManager
import com.michaelflisar.toolbox.app.platform.fileLogger
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
        debugDrawer = { state ->
            DebugDrawer(state) {
                // custom debug drawer content
            }
        },
        // TODO HELLO WORLD
        // Link anpassen
        privacyPolicyLink = "https://mflisar.github.io/android/flash-launcher/privacy-policy/",
        fileLogger = Platform.fileLogger,
        disableLanguagePicker = false,
        changelogSetup = ChangelogDefaults.setup(
            logFileReader = { Res.readBytes(Constants.CHANGELOG_PATH) },
            versionFormatter = Constants.CHANGELOG_FORMATTER
        ),
        isDebugBuild = isDebugBuild
    )

    // -------------------------
    // Pages
    // -------------------------

    val defaultPage = ScreenPage1

    override val pagesMain: List<INavScreen> =
        listOf(ScreenPage1, ScreenPage2)
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