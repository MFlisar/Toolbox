package com.michaelflisar.toolbox.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.application
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.DesktopAppContent
import com.michaelflisar.toolbox.app.DesktopStatusBar
import com.michaelflisar.toolbox.app.DesktopTitleBar
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.features.dialogs.ErrorDialogProvider
import com.michaelflisar.toolbox.app.features.dialogs.LocalErrorDialogState
import com.michaelflisar.toolbox.app.features.dialogs.rememberErrorDialogState
import com.michaelflisar.toolbox.app.features.dialogs.show
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.JvmNavigationUtil
import com.michaelflisar.toolbox.app.features.navigation.NavigationUtil
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManagerDisabled
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.utils.JvmUtil

fun main() {

    val prefs = BasePrefs(Preferences.createStorage("settings"))
    val setup = SharedDefinitions.createBaseAppSetup(
        prefs = prefs,
        debugStorage = Preferences.createStorage("debug"),
        proVersionManager = ProVersionManagerDisabled,
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
    val errorDialogState = LocalErrorDialogState.current
    return JvmNavigationUtil.getDesktopMenuItems(
        definition = SharedDefinitions
    ) + MenuItem.Group(
        text = "Test",
        icon = Icons.Default.Folder.toIconComposable(),
        items = listOf(
            MenuItem.Item(
                "Error Dialog Test",
                Icons.Default.Error.toIconComposable()
            ) {
                errorDialogState.show("Test Error", "This is a test error message")
            },
            MenuItem.Separator(text = "Group 1"),
            MenuItem.Item(
                "Action 1",
                Icons.Default.Folder.toIconComposable()
            ) {
                // ...
            },
            MenuItem.Item(
                "Action 2",
                Icons.Default.Folder.toIconComposable()
            ) {
                // ...
            },
            MenuItem.Separator(text = "Group 2"),
            MenuItem.Item(
                "Action 3",
                Icons.Default.Folder.toIconComposable()
            ) {
                // ...
            },
            MenuItem.Item(
                "Action 4",
                Icons.Default.Folder.toIconComposable()
            ) {
                // ...
            },
        )
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
