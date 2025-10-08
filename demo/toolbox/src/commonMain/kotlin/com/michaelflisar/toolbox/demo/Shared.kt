package com.michaelflisar.toolbox.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerButton
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerRegion
import com.michaelflisar.composethemer.FlatUIThemes
import com.michaelflisar.composethemer.Material500Themes
import com.michaelflisar.composethemer.MetroThemes
import com.michaelflisar.composethemer.themes.DefaultThemes
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.AppScope
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.Constants
import com.michaelflisar.toolbox.app.classes.Developer
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.DebugDrawer
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.update.UpdateManager
import com.michaelflisar.toolbox.demo.pages.PageHomeScreen
import com.michaelflisar.toolbox.demo.pages.PageSettingsScreen
import com.michaelflisar.toolbox.demo.pages.PageStatesScreen
import com.michaelflisar.toolbox.demo.pages.PageTestsRootScreenContainer
import com.michaelflisar.toolbox.demo.pages.tests.PageTestExpandableHeader
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.app_name
import com.michaelflisar.toolbox.demo.resources.mflisar
import com.michaelflisar.toolbox.extensions.isLight
import com.michaelflisar.toolbox.extensions.toIconComposable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

object Shared {

    // --------------------
    // Setup
    // --------------------

    /*
     * Language Picker:
     *      - res/resources.properties file erstellen
     *      - androidResources { generateLocaleConfig = true } im build.gradle.kts
     *      => sonst geht der language picker nicht
     */
    fun createBaseAppSetup(
        developer: Developer = Developer.Author(
            Constants.DEVELOPER_NAME,
            Constants.DEVELOPER_EMAIL
        ),
        prefs: BasePrefs,
        debugPrefs: DebugPrefs,
        icon: @Composable () -> Painter = { appIcon(LocalContentColor.current.isLight()) },
        isDebugBuild: Boolean,
        fileLogger: FileLogger<*>?,
    ): AppSetup {
        return AppSetup(
            developer = developer,
            versionCode = BuildKonfig.versionCode,
            versionName = BuildKonfig.versionName,
            packageName = BuildKonfig.packageName,
            name = Res.string.app_name,
            icon = icon,
            themeSupport = AppSetup.ThemeSupport.full(
                DefaultThemes.getAllThemes() +
                        FlatUIThemes.getAllThemes() +
                        MetroThemes.getAllThemes() +
                        Material500Themes.getAllThemes()
            ),
            prefs = prefs,
            debugPrefs = debugPrefs,
            debugDrawer = { state ->
                DebugDrawer(state) {
                    val appState = LocalAppState.current
                    DebugDrawerRegion(
                        image = { Icon(Icons.Default.Info, null) },
                        label = "Test",
                        drawerState = state
                    ) {
                        DebugDrawerButton(label = "Test") {
                            appState.showToast("Test clicked")
                        }
                    }
                }
            },
            privacyPolicyLink = "https://mflisar.github.io/android/flash-launcher/privacy-policy/",
            fileLogger = fileLogger,
            disableLanguagePicker = false,
            changelogSetup = ChangelogDefaults.setup(
                logFileReader = { Res.readBytes(Constants.CHANGELOG_PATH) },
                versionFormatter = Constants.CHANGELOG_FORMATTER
            ),
            isDebugBuild = isDebugBuild
        )
    }

    @Composable
    fun appIcon(light: Boolean): Painter {
        return painterResource(Res.drawable.mflisar)
    }

    // --------------------
    // Pages
    // --------------------

    // Main Pages
    val page1 = PageHomeScreen
    val page2 = PageStatesScreen
    val page3 = PageTestsRootScreenContainer
    val page4 = PageTestExpandableHeader
    val pages = listOf(page1, page2, page3, page4)

    // Settings Page
    val pageSettings = PageSettingsScreen

    // --------------------
    // Functions
    // --------------------

    fun init(context: PlatformContext, setup: AppSetup = AppSetup.get()) {

        // 1) App Data ggf. updaten
        val updateManager = UpdateManager(
            listOf(
                //UpdateTo1,
            )
        )
        updateManager.update(AppScope, context, setup)

        // 2) inits
        ToolboxLogging.enableAll()
    }

    // -------------------------
    // Actions
    // -------------------------

    @Composable
    fun customStatusBarActions(): List<ActionItem.Action> = listOf(
        actionTest()
    )

    @Composable
    private fun actionTest(): ActionItem.Action {
        val appState = LocalAppState.current
        return ActionItem.Action(
            title = "GLOBAL TEST Action",
            icon = Icons.Default.ArrowRight.toIconComposable(),
            action = {
                appState.showSnackbar("Test clicked")
            }
        )
    }
}