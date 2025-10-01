package com.michaelflisar.helloworld

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.application
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.DesktopAppContent
import com.michaelflisar.toolbox.app.DesktopStatusBar
import com.michaelflisar.toolbox.app.DesktopTitleBar
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.classes.resetWindowPosition
import com.michaelflisar.toolbox.app.classes.resetWindowSize
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState
import com.michaelflisar.toolbox.app.features.dialogs.ErrorDialogProvider
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavigationUtil
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage
import com.michaelflisar.toolbox.app.jewel.LocalJewelWindowState
import com.michaelflisar.toolbox.utils.JvmUtil

fun main() {

    val prefs = BasePrefs(Preferences.createStorage("settings"))
    val setup = SharedDefinitions.createBaseAppSetup(
        prefs = prefs,
        debugStorage = Preferences.createStorage("debug"),
        backupSupport = null,//JvmBackupSupport(),
        isDebugBuild = JvmUtil.isDebug()
    )
    val desktopSetup = DesktopAppSetup(
        prefs = DesktopPrefs(Preferences.createStorage("windows"))
    )

    SharedDefinitions.update(PlatformContext.NONE, setup)

    application {
        AppNavigator(
            screen = SharedDefinitions.defaultPage
        ) { navigator ->
            DesktopApp(
                setup = setup,
                desktopSetup = desktopSetup,
                navigator = navigator
            ) {
                ToolboxLogging.enableAll()

                // theme + root (drawer state, app state) are available
                ErrorDialogProvider {
                    DesktopAppContent(
                        navigationItems = provideNavigationItems(),
                        titlebar = {
                            DesktopTitleBar(
                                menuItems = provideMenuItems()
                            )
                        },
                        statusbar = { DesktopStatusBar() }
                    )
                }
            }
        }
    }
}

@Composable
private fun provideMenuItems(): List<MenuItem> {
    val jewelAppState = LocalJewelAppState.current
    val windowState = LocalJewelWindowState.current
    val density = LocalDensity.current
    return NavigationUtil.getDesktopMenuItems(
        definition = SharedDefinitions,
        resetWindowPosition = {
            jewelAppState.windowState.resetWindowPosition(
                density,
                windowState
            )
        },
        resetWindowSize = { jewelAppState.windowState.resetWindowSize() }
    )
}

@Composable
private fun provideNavigationItems(): List<INavItem> {
    return NavigationUtil.getRailNavigationItems(
        definition = SharedDefinitions,
        regionLabelMainPages = "Pages",
        regionLabelMainActions = "Actions"
    )
}
