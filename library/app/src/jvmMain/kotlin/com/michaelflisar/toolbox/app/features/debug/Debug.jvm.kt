package com.michaelflisar.toolbox.app.features.debug

internal actual fun processMemoryUsageBytes(): Long? {
    val runtime = Runtime.getRuntime()
    return runtime.totalMemory() - runtime.freeMemory()
}
