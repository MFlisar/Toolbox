package com.michaelflisar.toolbox.app.platform

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.composethemer.LocalComposeThemeState
import com.michaelflisar.kmp.platformcontext.PlatformContext
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.toolbar.toolbar

expect val Platform.showToast: ((message: String, duration: Int) -> Unit)?
expect val Platform.kill: ((context: PlatformContext) -> Unit)?
expect val Platform.restart: ((context: PlatformContext) -> Unit)?

@Composable
expect fun Platform.UpdateComposeThemeStatusBar(
    statusBarColor: Color = MaterialTheme.colorScheme.toolbar,
    navigationBarColor: Color = NavigationBarDefaults.containerColor,
    isDark: Boolean = LocalComposeThemeState.current.base.value.isDark(),
)

@Composable
internal expect fun Platform.isDarkTheme(): Boolean

@Composable
fun isAppInDarkTheme() = Platform.isDarkTheme()