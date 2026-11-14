package com.michaelflisar.toolbox.app.features.system

import com.michaelflisar.toolbox.utils.JvmUtil

actual val System.restart: (() -> Unit)?
    get() = null

actual val System.kill: (() -> Unit)?
    get() = { JvmUtil.killApp() }
