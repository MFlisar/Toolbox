package com.michaelflisar.toolbox.app.features.system


actual val System.restart: (() -> Unit)?
    get() = null

actual val System.kill: (() -> Unit)?
    get() = null
