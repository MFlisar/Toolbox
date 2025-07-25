package com.michaelflisar.toolbox.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composethemer.FlatUIThemes
import com.michaelflisar.composethemer.Material500Themes
import com.michaelflisar.composethemer.MetroThemes
import com.michaelflisar.composethemer.themes.DefaultThemes
import com.michaelflisar.kotpreferences.core.classes.BaseStorage
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppDefaults
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.backup.IBackupSupport
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManagerDisabled
import com.michaelflisar.toolbox.app.platform.AppPrefs
import com.michaelflisar.toolbox.app.platform.fileLogger
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.app_name
import com.michaelflisar.toolbox.demo.resources.mflisar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

object Shared {

    @Composable
    fun appIcon(): Painter {
        return painterResource(Res.drawable.mflisar)
    }

    fun createBaseAppSetup(
        prefs: AppPrefs,
        debugStorage: BaseStorage,
        icon: @Composable () -> Painter = { appIcon() },
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
        proVersionManager = ProVersionManagerDisabled,
        supportsChangelog = true,
        supportDebugDrawer = true,
        privacyPolicyLink = "https://mflisar.github.io/android/flash-launcher/privacy-policy/",
        supportLanguagePicker = true,
        fileLogger = Platform.fileLogger,
        changelogSetup = ChangelogDefaults.setup(
            logFileReader = { Res.readBytes(AppDefaults.CHANGELOG_PATH) },
            versionFormatter = AppDefaults.CHANGELOG_FORMATTER
        ),
        backupSupport = backupSupport,
        isDebugBuild = isDebugBuild
    )

    // -------------------------
    // Navigation and Menu Items
    // -------------------------

    @Composable
    private fun pagesMain() = listOf(
        SharedActions.pageHome(),
        SharedActions.page2(),
        SharedActions.pageMultiLevelRoot()
    )

    @Composable
    private fun pageSetting() = SharedActions.pageSettings()

    @Composable
    private fun actionsMain() = listOf(
        SharedActions.actionTest()
    )

    @Composable
    private fun actionsMenu() = listOf(
        SharedActions.actionProVersion(),
        SharedActions.actionChangelog()
    )

    @Composable
    fun provideNavigationItems(): List<INavItem> {
        return AppDefaults.provideNavigationItems(
            pagesMain = pagesMain(),
            pageSetting = pageSetting(),
            actionsMain = actionsMain(),
            actionsMenu = actionsMenu()
        )
    }

    @Composable
    fun provideAppMenu(
        resetWindowSize: (suspend () -> Unit)? = null,
        resetWindowPosition: (suspend () -> Unit)? = null,
    ): List<MenuItem> {
        return AppDefaults.provideAppMenu(
            pagesMain = pagesMain(),
            pageSetting = pageSetting(),
            actionsMain = actionsMain(),
            actionsMenu = actionsMenu(),
            resetWindowSize = resetWindowSize,
            resetWindowPosition = resetWindowPosition
        )
    }
}