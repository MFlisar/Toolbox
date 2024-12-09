package com.michaelflisar.toolbox.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T> MyThemeSwitcher(
    modifier: Modifier = Modifier,
    dark: T & Any,
    light: T & Any,
    system: T & Any,
    iconDark: @Composable () -> Unit = @Composable { Icon(Icons.Default.DarkMode, null) },
    iconLight: @Composable () -> Unit = @Composable { Icon(Icons.Default.LightMode, null) },
    iconSystem: @Composable () -> Unit = @Composable { Icon(Icons.Default.PhoneAndroid, null) },
    selected: T,
    mapper: (item: T & Any) -> String,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: (T) -> Unit
) {
    MySegmentedButtonRow(
        modifier = modifier,
        items = listOf(light, dark, system),
        icons = listOf(iconLight, iconDark, iconSystem),
        selected = selected,
        forceSelection = true,
        mapper = mapper,
        color = color,
        onColor = onColor,
        onSelectionChanged = onSelectionChanged
    )
}