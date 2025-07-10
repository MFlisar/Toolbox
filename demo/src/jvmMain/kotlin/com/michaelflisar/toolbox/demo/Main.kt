package com.michaelflisar.toolbox.demo

import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.appDir
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.resetWindowPosition
import com.michaelflisar.toolbox.app.classes.resetWindowSize
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState
import com.michaelflisar.toolbox.demo.pages.PageHomeScreen

fun main() {

    val setup = Shared.createBaseAppSetup(
        prefs = Prefs,
        debugStorage = DataStoreStorage.create(folder = Toolbox.appDir(), name = "debug")
    )
    val desktopSetup = DesktopAppSetup()

    DesktopApp(
        setup = setup,
        desktopSetup = desktopSetup,
        screen = PageHomeScreen,
        navigationItems = {
            Shared.provideNavigationItems()
        },
        menuItems = {
            val jewelAppState = LocalJewelAppState.current
            Shared.provideAppMenu(
                resetWindowSize = { jewelAppState.windowState.resetWindowSize() },
                resetWindowPosition = { jewelAppState.windowState.resetWindowPosition() }
            )
        }
    )
}
