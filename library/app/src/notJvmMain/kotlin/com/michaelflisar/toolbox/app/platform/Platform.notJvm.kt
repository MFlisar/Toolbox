package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.CommonApp

@Composable
actual fun Platform.isDarkTheme(): Boolean {
    val theme = CommonApp.setup.prefs.theme.collectAsStateNotNull()
    return theme.value.isDark()
}