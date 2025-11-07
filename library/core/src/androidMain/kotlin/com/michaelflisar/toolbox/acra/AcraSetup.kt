package com.michaelflisar.toolbox.acra

class AcraSetup(
    val appIcon: Int,
    val appName: String,
    val crashDialogText: String = "An unexpected error occurred. Please help me to fix this by sending me the error data by clicking OK.",
    val crashDialogTitle: String = "Unfortunately, $appName has crashed",
)