package com.michaelflisar.toolbox.app.features.system

import com.michaelflisar.kmp.platformcontext.PlatformApplicationContext
import com.michaelflisar.toolbox.killApp
import com.michaelflisar.toolbox.requireActivity
import com.michaelflisar.toolbox.restartApp

actual val System.restart: (() -> Unit)?
    get() = { PlatformApplicationContext.requireActivity().restartApp() }

actual val System.kill: (() -> Unit)?
    get() = { PlatformApplicationContext.requireActivity().killApp() }
