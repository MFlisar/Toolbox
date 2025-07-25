package com.michaelflisar.toolbox.demo

import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.resetWindowPosition
import com.michaelflisar.toolbox.app.classes.resetWindowSize
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState
import com.michaelflisar.toolbox.app.features.backup.JvmBackupSupport
import com.michaelflisar.toolbox.demo.pages.PageHomeScreen
import com.michaelflisar.toolbox.utils.JvmUtil

fun main() {

    val setup = Shared.createBaseAppSetup(
        prefs = Prefs,
        debugStorage = DataStoreStorage.create(folder = JvmUtil.appDir(), name = "debug"),
        backupSupport = JvmBackupSupport(
            prefBackupPath = Prefs.backupPath
        ),
        isDebugBuild = JvmUtil.isDebug()
    )
    val desktopSetup = DesktopAppSetup()

    DesktopApp(
        setup = setup,
        desktopSetup = desktopSetup,
        screen = PageHomeScreen,
        navigationItems = { Shared.provideNavigationItems() },
        menuItems = {
            val jewelAppState = LocalJewelAppState.current
            Shared.provideAppMenu(
                resetWindowPosition = { jewelAppState.windowState.resetWindowPosition() },
                resetWindowSize = { jewelAppState.windowState.resetWindowSize() }
            )
        }
    )
}
