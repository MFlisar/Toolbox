package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.classes.PlatformContext

@Composable
actual fun Platform.localContext() = PlatformContext.NONE