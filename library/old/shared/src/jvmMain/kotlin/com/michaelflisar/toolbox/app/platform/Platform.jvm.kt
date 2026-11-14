package com.michaelflisar.toolbox.app.platform

import com.michaelflisar.toolbox.Platform

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = null