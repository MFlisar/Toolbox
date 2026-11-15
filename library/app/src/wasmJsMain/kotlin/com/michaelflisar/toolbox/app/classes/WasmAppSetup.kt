package com.michaelflisar.toolbox.app.classes

/** ATTENTION:
 * must match the div ids in index.html
 * e.g. [demo\app\wasm\src\wasmJsMain\resources\index.html]
 */
class WasmAppSetup(
    val contentDivId: String = "content",
    val loadingDivId: String = "loading",
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