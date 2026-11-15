package com.michaelflisar.toolbox.app.features.debug

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
internal actual fun processMemoryUsageBytes(): Long? =
    js("globalThis.performance?.memory?.usedJSHeapSize ?? null")
