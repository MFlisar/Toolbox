package com.michaelflisar.toolbox.app.platform

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.UpdateEdgeToEdgeDefault
import com.michaelflisar.composethemer.defaultScrim
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AndroidAppPrefs
import com.michaelflisar.toolbox.app.classes.FileLogger
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.features.toolbar.toolbar
import com.michaelflisar.toolbox.extensions.onColor
import com.michaelflisar.toolbox.findActivity
import com.michaelflisar.toolbox.killApp
import com.michaelflisar.toolbox.restartApp

actual typealias AppPrefs = AndroidAppPrefs

@Composable
actual fun Platform.localContext() = PlatformContext(LocalContext.current)

actual val Platform.fileLogger: FileLogger<*>?
    get() = FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.Daily.create(
            context = AppContext.context(),
            fileExtension = "txt"
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = ::showToast

actual val Platform.restart: ((context: PlatformContext) -> Unit)?
    get() = { context -> context.androidContext.findActivity().restartApp() }

actual val Platform.kill: ((context: PlatformContext) -> Unit)?
    get() = { context -> context.androidContext.findActivity().killApp() }

private fun showToast(
    message: String,
    duration: Int
) {
    Toast.makeText(
        AppContext.context(),
        message,
        duration
    ).show()
}

@Composable
actual fun Platform.UpdateComposeThemeStatusBar(activity: Any?, composeThemeState: ComposeTheme.State) {

    val activity = activity as ComponentActivity
    val statusBarColor = MaterialTheme.colorScheme.toolbar
    val navigationBarColor = NavigationBarDefaults.containerColor

    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isDark = composeThemeState.base.value.isDark()

    UpdateEdgeToEdgeDefault(
        activity = activity,
        themeState = composeThemeState,
        statusBarColor = statusBarColor,
        navigationBarColor = if (landscape) {
            SystemBarStyle.defaultScrim(activity.resources, isDark)
        } else navigationBarColor,
        isNavigationBarContrastEnforced = landscape
    )
}