package com.michaelflisar.demo

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.michaelflisar.demo.pages.tests.TestPrefs
import com.michaelflisar.kotpreferences.storage.keyvalue.LocalStorageKeyValueStorage
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.WasmApp
import com.michaelflisar.toolbox.app.WasmApplication
import com.michaelflisar.toolbox.app.WasmContainer
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorTransitionPlatformStyle

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
    val wasmSetup = WasmAppSetup()
    WasmApp.init(
        setup = setup
    )

    // 3) App Data ggf. updaten
    Shared.init(setup)

    // 4) Sonstige Initialisierungen
    val storageTest = LocalStorageKeyValueStorage.create(key = "test")
    App.registerSingleton(TestPrefs(storageTest))

    // 4) Application
    ComposeViewport(
        // mit container id geht es nicht --> wäre aber gut, dann würde ein Loader angezeigt werden, aktuell wird der nicht angezeigt...
        // viewportContainerId = wasmSetup.canvasElementId
    ) {
        Shared.Init()

        WasmApplication(
            screen = Shared.page1,
            wasmSetup = wasmSetup
        ) { navigator ->

            // theme + root (drawer state, app state) are available
            WasmContainer {
                Shared.Content(
                    navigator
                ) {
                    AppNavigatorTransitionPlatformStyle(navigator)
                }
            }
        }
    }
}