package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.classes.PlatformContext

@Composable
actual fun Platform.localContext() = PlatformContext.NONE

actual val Platform.restart: ((context: PlatformContext) -> Unit)?
    get() = null

actual val Platform.kill: ((context: PlatformContext) -> Unit)?
    get() = null

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = null