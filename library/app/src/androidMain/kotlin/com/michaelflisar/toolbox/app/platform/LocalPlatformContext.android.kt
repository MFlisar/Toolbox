package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.michaelflisar.kmp.platformcontext.PlatformContext

actual val LocalPlatformContext.current: PlatformContext
    @Composable
    get() = LocalContext.current