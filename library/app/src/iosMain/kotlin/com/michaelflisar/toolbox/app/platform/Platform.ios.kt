package com.michaelflisar.toolbox.app.platform

import com.michaelflisar.kmp.platformcontext.PlatformContext
import com.michaelflisar.toolbox.Platform

actual val Platform.restart: ((context: PlatformContext) -> Unit)?
    get() = null

actual val Platform.kill: ((context: PlatformContext) -> Unit)?
    get() = null

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = null