package com.michaelflisar.composedemobaseactivity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedemobaseactivity.classes.DemoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
inline fun <reified T: Enum<T>> DemoAppThemeRegion(
    regionId: Int,
    theme: DemoTheme<T>,
    crossinline onThemeChanged: suspend (theme: DemoTheme<T>) -> Unit,
    dynamicTheme: Boolean,
    expandedRegionsState: ExpandedRegionState,
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
            Text("Theme", modifier = Modifier.weight(1f))
            DemoSegmentedButtons(
                items = DemoTheme.Theme.entries,
                itemToText = { it.name },
                initialSelectedIndex = theme.theme.ordinal
            ) { _, item ->
                scope.launch(Dispatchers.IO) {
                    onThemeChanged(theme.copy(theme = item))
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Color Scheme", modifier = Modifier.weight(1f))
            DemoSegmentedButtons(
                items = enumValues<T>().toList(),
                itemToText = { it.name },
                initialSelectedIndex = theme.colorScheme.ordinal
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
            Text("Dynamic Theme", modifier = Modifier.weight(1f))
            DemoSegmentedButtons(
                items = listOf("Yes", "No"),
                initialSelectedIndex = if (dynamicTheme) 0 else 1
            ) {
                scope.launch(Dispatchers.IO) {
                    onThemeChanged(theme.copy(dynamic = it == 0))
                }
            }
        }
    }
}