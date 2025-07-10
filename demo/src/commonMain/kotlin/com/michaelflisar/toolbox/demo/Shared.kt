package com.michaelflisar.toolbox.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composethemer.FlatUIThemes
import com.michaelflisar.composethemer.Material500Themes
import com.michaelflisar.composethemer.MetroThemes
import com.michaelflisar.composethemer.themes.DefaultThemes
import com.michaelflisar.kotpreferences.core.classes.BaseStorage
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManagerDisabled
import com.michaelflisar.toolbox.app.platform.AppPrefs
import com.michaelflisar.toolbox.app.platform.fileLoggerSetup
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.app_name
import com.michaelflisar.toolbox.demo.resources.mflisar
import kotlinx.coroutines.launch
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
        icon: @Composable () -> Painter = { appIcon() }
    ) = AppSetup(
        versionCode = BuildKonfig.versionCode,
        versionName = BuildKonfig.versionName,
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
        fileLoggerSetup = Platform.fileLoggerSetup as? FileLoggerSetup,
        changelogSetup = ChangelogDefaults.setup(
            logFileReader = { Res.readBytes(CommonApp.changelogPath) },
            versionFormatter = CommonApp.changelogFormatter
        )
    )

    @Composable
    fun provideNavigationItems(): List<INavItem> {
        return when (CurrentDevice.base) {
            BaseDevice.Desktop,
            BaseDevice.Web -> {
                listOf(
                    NavItemRegion("Pages"),
                    SharedActions.pageHome().toNavItem(),
                    NavItemRegion("Actions"),
                    SharedActions.actionTest().toNavItem(),
                    NavItemSpacer(),
                    SharedActions.pageSettings().toNavItem()
                )
            }

            BaseDevice.Mobile -> {
                listOf(
                    SharedActions.pageHome().toNavItem()
                )
            }
        }
    }

    @Composable
    fun provideAppMenu(
        resetWindowSize: suspend () -> Unit = {},
        resetWindowPosition: suspend () -> Unit = {},
    ): List<MenuItem> {
        val scope = rememberCoroutineScope()
        return when (CurrentDevice.base) {
            BaseDevice.Desktop,
            BaseDevice.Web -> {
                val resetWindowMenuItems = listOf(
                    MenuItem.Item("Reset Window Size", Icons.Default.Clear) {
                        scope.launch {
                            resetWindowSize()
                        }
                    },
                    MenuItem.Item("Reset Window Position", Icons.Default.Clear) {
                        scope.launch {
                            resetWindowPosition()
                        }
                    }
                )
                listOf(
                    MenuItem.Group(
                        text = "App",
                        items = listOf(
                            SharedActions.actionProVersion().toMenuItem(),
                            SharedActions.actionChangelog().toMenuItem(),
                        )
                    ),
                    MenuItem.Group(
                        text = "Window",
                        items = resetWindowMenuItems
                    )
                )
            }

            BaseDevice.Mobile -> {
                listOf(
                    MenuItem.Group(
                        imageVector = Icons.Default.MoreVert,
                        items = listOf(
                            SharedActions.pageSettings().toMenuItem(),
                            SharedActions.actionProVersion().toMenuItem(),
                            SharedActions.actionChangelog().toMenuItem(),
                        )
                    )
                )
            }
        }
    }
}