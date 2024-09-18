package com.michaelflisar.composedemobaseactivity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedemobaseactivity.classes.DemoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
inline fun <reified T : Enum<T>> DemoAppThemeRegion(
    regionId: Int,
    theme: DemoTheme<T>,
    crossinline onThemeChanged: suspend (theme: DemoTheme<T>) -> Unit,
    expandedRegionsState: ExpandedRegionState
) {
    val scope = rememberCoroutineScope()
    DemoCollapsibleRegion(
        title = "App Theme",
        regionId = regionId,
        expandedRegionsState = expandedRegionsState
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.ColorLens, null)
            Text("Base Theme", modifier = Modifier.weight(1f))
            DemoSegmentedButtons(
                modifier = Modifier.weight(1f),
                items = enumValues<T>().toList(),
                itemToText = { it.name },
                initialSelectedIndex = theme.baseTheme.ordinal
            ) { _, item ->
                scope.launch(Dispatchers.IO) {
                    onThemeChanged(theme.copy(baseTheme = item))
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Style, null)
            Text("Color Scheme", modifier = Modifier.weight(1f))
            DemoDropdown(
                modifier = Modifier.weight(1f),
                items = theme.availableColorSchemes,
                selected = theme.colorScheme
            ) { _, item ->
                scope.launch(Dispatchers.IO) {
                    onThemeChanged(theme.copy(colorScheme = item))
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.FormatPaint, null)
            Text("Dynamic Theme", modifier = Modifier.weight(1f))
            DemoSegmentedButtons(
                modifier = Modifier.weight(1f),
                items = listOf("Yes", "No"),
                initialSelectedIndex = if (theme.dynamic) 0 else 1
            ) {
                scope.launch(Dispatchers.IO) {
                    onThemeChanged(theme.copy(dynamic = it == 0))
                }
            }
        }
    }
}