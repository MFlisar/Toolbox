package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.kmp.platformcontext.PlatformContext

object LocalPlatformContext

expect val LocalPlatformContext.current: PlatformContext
    @Composable
    get
