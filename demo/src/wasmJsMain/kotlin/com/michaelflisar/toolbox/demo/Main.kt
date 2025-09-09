package com.michaelflisar.toolbox.demo

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.michaelflisar.toolbox.app.WasmApp
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.NavigationUtil
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManagerDisabled

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
suspend fun main() {

    val prefs = BasePrefs(Preferences.createStorage("settings"))
    val setup = SharedDefinitions.createBaseAppSetup(
        prefs = prefs,
        debugStorage = Preferences.createStorage("debug"),
        proVersionManager = ProVersionManagerDisabled,
        backupSupport = null, // no backup support in wasm
        isDebugBuild = true // TODO: how to detect in wasm?
    )
    val wasmSetup = WasmAppSetup(
        title = "Toolbox"
    )

    SharedDefinitions.update(PlatformContext.NONE, setup)

    CanvasBasedWindow(wasmSetup.title, canvasElementId = wasmSetup.canvasElementId) {
        AppNavigator(
            screen = SharedDefinitions.defaultPage
        ) { navigator ->
            WasmApp(
                setup = setup,
                wasmSetup = wasmSetup,
                navigator = navigator,
                navigationItems = { NavigationUtil.getWebNavigationItems(SharedDefinitions) },
                menuItems = { NavigationUtil.getWebMenuItems(SharedDefinitions) }
            )
        }
    }
}