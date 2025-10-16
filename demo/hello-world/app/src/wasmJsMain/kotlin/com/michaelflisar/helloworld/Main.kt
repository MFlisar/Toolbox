package com.michaelflisar.helloworld

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.kotpreferences.storage.keyvalue.LocalStorageKeyValueStorage
import com.michaelflisar.toolbox.app.WasmApp
import com.michaelflisar.toolbox.app.WasmAppDefaults
import com.michaelflisar.toolbox.app.WasmApplication
import com.michaelflisar.toolbox.app.WasmScaffold
import com.michaelflisar.toolbox.app.WasmToolbar
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {

    // 1) Storages erstellen
    val storageSettings = LocalStorageKeyValueStorage.create(key = "settings")
    val storageDebug = LocalStorageKeyValueStorage.create(key = "debug")

    // 2) Setups erstellen
    val setup = Shared.createBaseAppSetup(
        prefs = Prefs(storageSettings),
        debugPrefs = DebugPrefs(storageDebug),
        isDebugBuild = true, // TODO: how to detect in wasm?
        fileLogger = null
    )
    val wasmSetup = WasmAppSetup(
        title = "Hello World"
    )
    WasmApp.init(
        setup = setup
    )

    // 3) App Data ggf. updaten
    Shared.init(PlatformContext.NONE, setup)

    // 4) Application
    CanvasBasedWindow(wasmSetup.title, canvasElementId = wasmSetup.canvasElementId) {

        WasmApplication(
            screen = Shared.page1,
            wasmSetup = wasmSetup
        ) { navigator ->

            // theme + root (drawer state, app state) are available
            val navigator = LocalNavigator.currentOrThrow
            WasmScaffold(
                topBar = {
                    WasmToolbar(
                        navigationItems = Shared.pages.map { it.toNavItem() },
                        menuItems = WasmAppDefaults.getWebMenuItems(
                            pageSettings = Shared.pageSettings
                        )
                    )
                },
            ) {
                AppNavigatorFadeTransition(navigator)
            }
        }
    }
}

