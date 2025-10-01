package com.michaelflisar.helloworld

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.michaelflisar.toolbox.app.WasmApp
import com.michaelflisar.toolbox.app.WasmAppContent
import com.michaelflisar.toolbox.app.WasmToolbar
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.NavigationUtil
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    val prefs = BasePrefs(Preferences.createStorage("settings"))
    val setup = SharedDefinitions.createBaseAppSetup(
        prefs = prefs,
        debugStorage = Preferences.createStorage("debug"),
        backupSupport = null, // no backup support in wasm
        isDebugBuild = true // TODO: how to detect in wasm?
    )
    // TODO HELLO WORLD
    // title anpassen
    val wasmSetup = WasmAppSetup(
        title = "Hello World"
    )

    SharedDefinitions.update(PlatformContext.NONE, setup)

    CanvasBasedWindow(wasmSetup.title, canvasElementId = wasmSetup.canvasElementId) {
        AppNavigator(
            screen = SharedDefinitions.defaultPage
        ) { navigator ->
            WasmApp(
                setup = setup,
                wasmSetup = wasmSetup,
                navigator = navigator
            ) {
                // theme + root (drawer state, app state) are available
                WasmAppContent(
                    toolbar = {
                        WasmToolbar(
                            navigationItems = {
                                NavigationUtil.getWebNavigationItems(
                                    SharedDefinitions
                                )
                            },
                            menuItems = { NavigationUtil.getWebMenuItems(SharedDefinitions) }
                        )
                    }
                )
            }
        }
    }
}