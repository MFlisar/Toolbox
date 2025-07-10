package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.interfaces.IAppPrefs

expect interface AppPrefs : IAppPrefs

@Composable
expect fun Platform.localContext() : PlatformContext

expect val Platform.fileLoggerSetup: IFileLoggingSetup?
expect val Platform.showToast: ((message: String, duration: Int) -> Unit)?
expect val Platform.kill: ((context: PlatformContext) -> Unit)?
expect val Platform.restart: ((context: PlatformContext) -> Unit)?