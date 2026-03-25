package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.kmp.platformcontext.PlatformContext
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import com.michaelflisar.kmp.platformcontext.getDefaultPlatformContext

actual val LocalPlatformContext.current: PlatformContext
    @Composable
    get() = PlatformContextProvider.getDefaultPlatformContext()!! // instance of PlatformContextEmpty (wäre nur auf android null)