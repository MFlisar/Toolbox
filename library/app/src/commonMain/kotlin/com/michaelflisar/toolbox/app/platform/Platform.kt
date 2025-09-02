package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.app.classes.PlatformContext

// Logging
expect val Platform.fileLogger: FileLogger<*>?

@Composable
expect fun Platform.localContext() : PlatformContext
expect val Platform.showToast: ((message: String, duration: Int) -> Unit)?
expect val Platform.kill: ((context: PlatformContext) -> Unit)?
expect val Platform.restart: ((context: PlatformContext) -> Unit)?

@Composable
expect fun Platform.UpdateComposeThemeStatusBar(activity: Any?, composeThemeState: ComposeTheme.State)

@Composable
expect fun Platform.isDarkTheme(): Boolean