package com.michaelflisar.toolbox.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.window.application
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
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
                navigator = navigator,
                navigationItems = {
                    NavigationUtil.getRailNavigationItems(
                        definition = SharedDefinitions,
                        regionLabelMainPages = "Pages",
                        regionLabelMainActions = "Actions"
                    )
                },
                menuItems = {
                    JvmNavigationUtil.getDesktopMenuItems(
                        definition = SharedDefinitions
                    ) + MenuItem.Group(
                        text = "Test",
                        icon = Icons.Default.Folder.toIconComposable(),
                        items = listOf(
                            MenuItem.Separator(text = "Group 1"),
                            MenuItem.Item("Action 1", Icons.Default.Folder.toIconComposable()) {
                                // ...
                            },
                            MenuItem.Item("Action 2", Icons.Default.Folder.toIconComposable()) {
                                // ...
                            },
                            MenuItem.Separator(text = "Group 2"),
                            MenuItem.Item("Action 3", Icons.Default.Folder.toIconComposable()) {
                                // ...
                            },
                            MenuItem.Item("Action 4", Icons.Default.Folder.toIconComposable()) {
                                // ...
                            },
                        )
                    )
                }
            )
        }
    }
}
