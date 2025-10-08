package com.michaelflisar.toolbox.app

import androidx.activity.ComponentActivity
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

object AndroidApp {

    fun init(activity: ComponentActivity) {
        FileKit.init(activity)
    }
}