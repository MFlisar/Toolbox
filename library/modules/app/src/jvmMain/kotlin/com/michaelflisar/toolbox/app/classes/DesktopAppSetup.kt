package com.michaelflisar.toolbox.app.classes

import androidx.compose.ui.input.key.NativeKeyEvent

class DesktopAppSetup(
    val visible: Boolean = true,
    val resizable: Boolean = true,
    val enabled: Boolean = true,
    val focusable: Boolean = true,
    val swingCompatMode: Boolean = false,
    val onClosed: (suspend () -> Unit)? = null,
    val onPreviewKeyEvent: (NativeKeyEvent) -> Boolean = { false },
    val onKeyEvent: (NativeKeyEvent) -> Boolean = { false },
)