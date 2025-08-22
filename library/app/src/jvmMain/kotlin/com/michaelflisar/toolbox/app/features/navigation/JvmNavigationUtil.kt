package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import com.michaelflisar.toolbox.app.classes.resetWindowPosition
import com.michaelflisar.toolbox.app.classes.resetWindowSize
import com.michaelflisar.toolbox.app.features.appstate.JewelAppState
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.jewel.LocalJewelWindowState

object JvmNavigationUtil {
    @Composable
    fun getDesktopMenuItems(
        definition: INavigationDefinition,
        menuLabel: String = "App",
        menuWindowLabel: String = "Window",
        labelResetWindowSize: String = "Reset Window Size",
        labelResetWindowPosition: String = "Reset Window Position",
        labelOpenDebugDrawer: String = "Open Debug Drawer",
        labelCloseDebugDrawer: String = "Close Debug Drawer"
    ): List<MenuItem> {
        val jewelAppState = LocalJewelAppState.current
        val windowState = LocalJewelWindowState.current
        val density = LocalDensity.current
        return NavigationUtil.getDesktopMenuItems(
            definition = definition,
            menuLabel = menuLabel,
            menuWindowLabel = menuWindowLabel,
            labelResetWindowSize = labelResetWindowSize,
            labelResetWindowPosition = labelResetWindowPosition,
            labelOpenDebugDrawer = labelOpenDebugDrawer,
            labelCloseDebugDrawer = labelCloseDebugDrawer,
            resetWindowPosition = { jewelAppState.windowState.resetWindowPosition(density, windowState) },
            resetWindowSize = { jewelAppState.windowState.resetWindowSize() }
        )
    }
}