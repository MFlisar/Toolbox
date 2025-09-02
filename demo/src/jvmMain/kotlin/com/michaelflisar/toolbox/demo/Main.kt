package com.michaelflisar.toolbox.demo

import androidx.compose.ui.window.application
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.features.navigation.JvmNavigationUtil
import com.michaelflisar.toolbox.app.features.navigation.NavigationUtil
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManagerDisabled
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

        DesktopApp(
            setup = setup,
            desktopSetup = desktopSetup,
            screen = SharedDefinitions.defaultPage,
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
                )
            }
        )
    }
}
