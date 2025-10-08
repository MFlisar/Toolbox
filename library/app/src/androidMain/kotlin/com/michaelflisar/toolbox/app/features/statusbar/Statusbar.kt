package com.michaelflisar.toolbox.app.features.statusbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import com.michaelflisar.toolbox.extensions.isDark
import com.michaelflisar.toolbox.requireActivity

@Composable
internal actual fun UpdateStatusBarColor(background: Color) {
    val context = LocalContext.current
    val activity = context.requireActivity()
    val window = activity.window
    LaunchedEffect(background) {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = !background.isDark()
    }
}