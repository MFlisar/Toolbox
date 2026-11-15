package com.michaelflisar.toolbox.app.features.debug

import android.os.Debug

internal actual fun processMemoryUsageBytes(): Long? {
    val memoryInfo = Debug.MemoryInfo()
    Debug.getMemoryInfo(memoryInfo)
    return memoryInfo.totalPss.toLong() * 1024L
}
