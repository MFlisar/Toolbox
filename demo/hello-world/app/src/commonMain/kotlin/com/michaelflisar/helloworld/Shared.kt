package com.michaelflisar.helloworld

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composethemer.FlatUIThemes
import com.michaelflisar.composethemer.Material500Themes
import com.michaelflisar.composethemer.MetroThemes
import com.michaelflisar.composethemer.themes.DefaultThemes
import com.michaelflisar.helloworld.app.BuildKonfig
import com.michaelflisar.helloworld.core.resources.app_name
import com.michaelflisar.helloworld.core.resources.mflisar
import com.michaelflisar.helloworld.feature.page1.ScreenPage1
import com.michaelflisar.helloworld.feature.page2.ScreenPage2
import com.michaelflisar.toolbox.app.AppScope
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.Constants
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.DebugDrawer
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.update.UpdateManager
import com.michaelflisar.toolbox.extensions.isLight
import com.michaelflisar.toolbox.extensions.toIconComposable
import org.jetbrains.compose.resources.painterResource
import com.michaelflisar.helloworld.PageSettingsScreen
import com.michaelflisar.helloworld.core.resources.Res

object Shared {

    // --------------------
    // Setup
    // --------------------

    /*
     * Language Picker:
     *   - res/resources.properties file erstellen
     *   - androidResources { generateLocaleConfig = true } im build.gradle.kts
     *   => sonst geht der language picker nicht
     */
    fun createBaseAppSetup(
        prefs: BasePrefs,
        debugPrefs: DebugPrefs,
        icon: @Composable () -> Painter = { appIcon(LocalContentColor.current.isLight()) },
        isDebugBuild: Boolean,
        fileLogger: FileLogger<*>?
    ) = AppSetup(
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
                // custom debug drawer content
            }
        },
        // TODO HELLO WORLD
        // Link anpassen
        privacyPolicyLink = "https://mflisar.github.io/android/flash-launcher/privacy-policy/",
        fileLogger = fileLogger,
        disableLanguagePicker = false,
        changelogSetup = ChangelogDefaults.setup(
            logFileReader = { Res.readBytes(Constants.CHANGELOG_PATH) },
            versionFormatter = Constants.CHANGELOG_FORMATTER
        ),
        isDebugBuild = isDebugBuild
    )

    @Composable
    fun appIcon(light: Boolean): Painter {
        return painterResource(Res.drawable.mflisar)
    }

    // --------------------
    // Pages
    // --------------------

    val page1 = ScreenPage1
    val page2 = ScreenPage2

    val pages = listOf(page1, page2)

    val pageSettings = PageSettingsScreen

    // --------------------
    // Functions
    // --------------------


    fun init(context: PlatformContext) {
        val updateManager = UpdateManager(
            listOf(
                //UpdateTo1,
            )
        )
        updateManager.update(AppScope, context, AppSetup.get())
    }

    // -------------------------
    // Actions
    // -------------------------

    @Composable
    fun actionCustom(): List<ActionItem.Action> = listOf(
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