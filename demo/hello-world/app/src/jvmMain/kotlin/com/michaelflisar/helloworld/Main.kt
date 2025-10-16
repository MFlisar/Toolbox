package com.michaelflisar.helloworld

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.window.application
import com.michaelflisar.helloworld.app.BuildKonfig
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.DesktopApplication
import com.michaelflisar.toolbox.app.DesktopNavigation
import com.michaelflisar.toolbox.app.DesktopScaffold
import com.michaelflisar.toolbox.app.DesktopStatusBar
import com.michaelflisar.toolbox.app.DesktopTitleBar
import com.michaelflisar.toolbox.app.DesktopTitleMenu
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.navigation.NavItemPopupMenu
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.app.features.scaffold.NavigationStyle
import com.michaelflisar.toolbox.app.features.scaffold.rememberNavigationStyleAuto
import com.michaelflisar.toolbox.app.utils.createFileLogger
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_more
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.rememberMenuState
import com.michaelflisar.toolbox.utils.JvmFolderUtil
import com.michaelflisar.toolbox.utils.JvmUtil
import org.jetbrains.compose.resources.stringResource

fun main() {

    // 1) Pfade
    val dataFolder = JvmFolderUtil.getPathForAppData(BuildKonfig.packageName)

    // 2) Storages erstellen
    val storageSettings = DataStoreStorage.create(folder = dataFolder, name = "settings")
    val storageDebug = DataStoreStorage.create(folder = dataFolder, name = "debug")
    val storageWindows = DataStoreStorage.create(folder = dataFolder, name = "windows")

    // 3) Setups erstellen
    val setup = Shared.createBaseAppSetup(
        prefs = Prefs(storageSettings),
        debugPrefs = DebugPrefs(storageDebug),
        isDebugBuild = JvmUtil.isDebug(),
        fileLogger = JvmUtil.createFileLogger(folder = dataFolder)
    )
    val desktopSetup = DesktopAppSetup(
        prefs = DesktopPrefs(storageWindows),
        titleBarIcon = { light -> Shared.appIcon(light = light) }, // icon in title bar
        appIcon = { Shared.appIcon(light = true) }  // icon in windows toolbar
    )
    DesktopApp.init(
        setup = setup,
        desktopSetup = desktopSetup
    )

    // 4) App Data ggf. updaten
    Shared.init(PlatformContext.NONE)

    // 5) Application
    application {

        DesktopApplication(
            screen = Shared.page1
        ) { navigator ->

            // theme + root (drawer state, app state) are available now

            // Scaffold
            val navigationStyle = rememberNavigationStyleAuto()
            DesktopScaffold(
                titleBar = {
                    DesktopTitleBar {
                        DesktopTitleMenu(
                            items = Shared.actionCustom().map { it.toMenuItem() }
                        )
                    }
                },
                statusBar = {
                    DesktopStatusBar()
                },
                navigationStyle = navigationStyle,
                navigation = {
                    val menu = rememberMenuState()
                    DesktopNavigation(
                        navigationStyle = navigationStyle,
                        items = Shared.pages.map { it.toNavItem() },
                        additionalItems = {
                            when (it) {
                                NavigationStyle.Left -> listOf(Shared.pageSettings.toNavItem())
                                NavigationStyle.Bottom -> listOf(
                                    NavItemPopupMenu(
                                        title = stringResource(Res.string.menu_more),
                                        icon = Icons.Default.MoreVert.toIconComposable(),
                                        state = menu
                                    ) {
                                        PopupMenu(state = menu) {
                                            Shared.pageSettings.PopupMenuItem(this)
                                        }
                                    }
                                )
                            }
                        },
                        alwaysShowBottomLabel = false,
                        showAdditionalItemsAtBottomIfRail = true,
                    )
                }
            ) {
                AppNavigatorFadeTransition(navigator)
            }
        }
    }
}
