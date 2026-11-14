package com.michaelflisar.demo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.michaelflisar.kotpreferences.storage.keyvalue.LocalStorageKeyValueStorage
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.WasmApplication
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.init

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {

    // 1) Storages erstellen
    val storageSettings = LocalStorageKeyValueStorage.create(key = "settings")
    val storageDebug = LocalStorageKeyValueStorage.create(key = "debug")

    // 2) Setups erstellen
    val setup = Shared.createSetup(
        storageSettings = storageSettings,
        storageDebug = storageDebug,
        fileLoggingSetup = null,
        isDebugBuild = true, // TODO: how to detect in wasm?
    )
    val wasmSetup = WasmAppSetup()

    // 3) App initialisieren
    App.init(setup, wasmSetup)
    Shared.init()

    // 4) Application
    ComposeViewport {
        WasmApplication {
            DemoApp(
                desktopMenuBar = true,
                forwardHistoryEnabled = true
            )
        }
    }
}