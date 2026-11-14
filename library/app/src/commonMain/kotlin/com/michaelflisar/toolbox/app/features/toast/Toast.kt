package com.michaelflisar.toolbox.app.features.toast

enum class ToastDuration {
    Short,
    Long
}

expect val showToast: ((message: String, duration: ToastDuration) -> Unit)?