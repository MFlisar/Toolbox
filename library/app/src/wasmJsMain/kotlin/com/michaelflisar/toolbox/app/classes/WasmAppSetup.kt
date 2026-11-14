package com.michaelflisar.toolbox.app.classes

class WasmAppSetup(
    val canvasElementId: String = "ComposeTarget",
    val divLoadingElementId: String = "ComposeLoading",
) {
    companion object {
        private var instance: WasmAppSetup? = null

        internal fun init(setup: WasmAppSetup): Boolean {
            if (instance != null)
                return false

            instance = setup
            return true
        }

        fun get(): WasmAppSetup =
            instance ?: error("WasmAppSetup.init(setup) must be called before accessing it.")
    }
}