package com.michaelflisar.toolbox.app.platform

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.UpdateEdgeToEdgeDefault
import com.michaelflisar.composethemer.defaultScrim
import com.michaelflisar.kmp.platformcontext.PlatformContext
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.toolbar.toolbar
import com.michaelflisar.toolbox.killApp
import com.michaelflisar.toolbox.requireActivity
import com.michaelflisar.toolbox.restartApp

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = ::showToast

actual val Platform.restart: ((context: PlatformContext) -> Unit)?
    get() = { context -> context.requireActivity().restartApp() }

actual val Platform.kill: ((context: PlatformContext) -> Unit)?
    get() = { context -> context.requireActivity().killApp() }

private fun showToast(
    message: String,
    duration: Int,
) {
    Toast.makeText(
        PlatformContextProvider.get(),
        message,
        duration
    ).show()
}

@Composable
actual fun Platform.UpdateComposeThemeStatusBar(
    activity: Any?,
    composeThemeState: ComposeTheme.State,
) {

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