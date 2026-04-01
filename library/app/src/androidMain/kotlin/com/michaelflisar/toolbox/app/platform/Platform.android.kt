package com.michaelflisar.toolbox.app.platform

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.michaelflisar.composethemer.LocalComposeThemeState
import com.michaelflisar.composethemer.UpdateEdgeToEdgeDefault
import com.michaelflisar.composethemer.defaultScrim
import com.michaelflisar.kmp.platformcontext.PlatformContext
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.activity.LocalActivity
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
    statusBarColor: Color,
    navigationBarColor: Color,
    isDark: Boolean,
) {
    val composeThemeState = LocalComposeThemeState.current
    val activity = LocalActivity.current
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
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