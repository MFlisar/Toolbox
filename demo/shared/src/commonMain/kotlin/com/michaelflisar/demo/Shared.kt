package com.michaelflisar.demo

import androidx.compose.material.icons.Icons
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
import com.michaelflisar.demo.pages.PageHomeScreen
import com.michaelflisar.demo.pages.PageSelectionScreen
import com.michaelflisar.demo.pages.PageSettingsScreen
import com.michaelflisar.demo.pages.PageStatesScreen
import com.michaelflisar.demo.pages.PageTestsRootScreenContainer
import com.michaelflisar.demo.pages.tests.PageTestExpandableHeader
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.AppScope
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.Constants
import com.michaelflisar.toolbox.app.classes.Developer
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.DebugDrawer
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.update.UpdateManager
import com.michaelflisar.toolbox.demo.BuildKonfig
import com.michaelflisar.toolbox.demo.shared.resources.Res
import com.michaelflisar.toolbox.demo.shared.resources.icon
import com.michaelflisar.toolbox.extensions.isLight
import org.jetbrains.compose.resources.painterResource

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
            appData = AppSetup.AppData(
                versionCode = BuildKonfig.versionCode,
                versionName = BuildKonfig.versionName,
                namespace = BuildKonfig.namespace,
                name = BuildKonfig.appName
            ),
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
        return painterResource(Res.drawable.icon)
    }

    // --------------------
    // Pages
    // --------------------

    // Main Pages
    val page1 = PageHomeScreen
    val page2 = PageStatesScreen
    val page3 = PageTestsRootScreenContainer
    val page4 = PageTestExpandableHeader
    val page5 = PageSelectionScreen
    val pages = listOf(page1, page2, page3, page4, page5)

    // Settings Page
    val pageSettings = PageSettingsScreen

    // --------------------
    // Functions
    // --------------------

    fun init(setup: AppSetup = AppSetup.get()) {

        // 1) App Data ggf. updaten
        val updateManager = UpdateManager(
            listOf(
                //UpdateTo1,
            )
        )
        updateManager.update(AppScope, setup)

        // 2) inits
        ToolboxLogging.enableAll()
    }
}