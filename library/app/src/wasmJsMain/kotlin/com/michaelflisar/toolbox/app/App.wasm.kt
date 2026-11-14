package com.michaelflisar.toolbox.app

import com.michaelflisar.toolbox.app.classes.WasmAppSetup

fun App.init(
    setup: AppSetup,
    wasm: WasmAppSetup,
) {
    initAppSetup(setup)
    WasmAppSetup.init(wasm)
}