package com.michaelflisar.toolbox.app.features.toast

actual val showToast: ((message: String, duration: ToastDuration) -> Unit)?
    get() = null