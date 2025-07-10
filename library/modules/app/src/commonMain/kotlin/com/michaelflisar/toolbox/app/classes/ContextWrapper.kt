package com.michaelflisar.toolbox.app.classes

class PlatformContext(
    val context: Any
) {
    companion object {
        val NONE = PlatformContext(Unit)
    }
}