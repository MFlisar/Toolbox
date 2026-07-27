package com.michaelflisar.toolbox.app.platform

import com.michaelflisar.kmp.platformcontext.PlatformContext
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.utils.JvmUtil

actual val Platform.restart: ((context: PlatformContext) -> Unit)?
    get() = null //{ JvmUtil.restartApp() }

actual val Platform.kill: ((context: PlatformContext) -> Unit)?
    get() = { JvmUtil.killApp() }

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = null