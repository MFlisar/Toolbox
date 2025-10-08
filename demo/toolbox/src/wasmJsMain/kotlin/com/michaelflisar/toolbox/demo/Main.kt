package com.michaelflisar.toolbox.demo

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.kotpreferences.storage.keyvalue.LocalStorageKeyValueStorage
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.WasmApp
import com.michaelflisar.toolbox.app.WasmAppDefaults
import com.michaelflisar.toolbox.app.WasmApplication
import com.michaelflisar.toolbox.app.WasmScaffold
import com.michaelflisar.toolbox.app.WasmToolbar
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.demo.pages.tests.TestPrefs

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
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
        title = "Toolbox"
    )
    WasmApp.init(
        setup = setup
    )

    // test prefs
    val storageTest = LocalStorageKeyValueStorage.create(key = "test")
    App.registerSingleton(TestPrefs(storageTest))

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