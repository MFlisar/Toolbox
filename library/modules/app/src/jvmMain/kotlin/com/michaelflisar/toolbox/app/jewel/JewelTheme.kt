package com.michaelflisar.toolbox.app.jewel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

enum class JewelBaseTheme {
    Light, Dark, System;
}

enum class JewelTheme(
    val imageVector: ImageVector,
    val label: String,
    val switchLabel: String,
) {
    Light(Icons.Default.LightMode, "Light", "Switch to light theme"),
    LightWithLightHeader(
        Icons.Default.LightMode,
        "Light with light header",
        "Switch to light theme with light header"
    ),
    Dark(Icons.Default.DarkMode, "Dark", "Switch to dark theme"),
    System(Icons.Default.Computer, "System", "Switch to system theme")
    ;

    fun baseTheme() = when (this) {
        Dark -> JewelBaseTheme.Dark
        Light, LightWithLightHeader -> JewelBaseTheme.Light
        System -> JewelBaseTheme.System
    }

    fun isDark() = (if (this == System) fromSystemTheme(currentSystemTheme) else this) == Dark

    fun isLightHeader() = this == LightWithLightHeader

    @Composable
    fun <T> select(dark: T, light: T) = if (isDark()) dark else light

    companion object {
        fun fromSystemTheme(systemTheme: SystemTheme) =
            if (systemTheme == SystemTheme.LIGHT) Light else Dark
    }
}