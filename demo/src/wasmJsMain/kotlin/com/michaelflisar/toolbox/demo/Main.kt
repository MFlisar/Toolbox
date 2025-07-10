package com.michaelflisar.toolbox.demo

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.michaelflisar.kotpreferences.storage.keyvalue.LocalStorageKeyValueStorage
import com.michaelflisar.toolbox.app.WasmApp
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.demo.pages.PageHomeScreen

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
suspend fun main() {

    val setup = Shared.createBaseAppSetup(
        prefs = Prefs,
        debugStorage = LocalStorageKeyValueStorage.create(key = "debug")
    )
    val wasmSetup = WasmAppSetup(
        title = "Toolbox Demo"
    )

    WasmApp(
        setup = setup,
        wasmSetup = wasmSetup,
        screen = PageHomeScreen,
        navigationItems = { Shared.provideNavigationItems() },
        menuItems = { Shared.provideAppMenu() },
    )
}