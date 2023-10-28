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
import com.michaelflisar.composedemobaseactivity.classes.AppPrefs
import com.michaelflisar.composedemobaseactivity.classes.DemoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DemoAppThemeRegion(
    theme: DemoTheme,
    dynamicTheme: Boolean,
    id: Int,
    expandedIds: SnapshotStateList<Int>,
) {
    DemoAppThemeRegion(
        theme,
        dynamicTheme,
        isExpanded = expandedIds.contains(id),
        onExpandedChanged = {
            if (it) expandedIds.remove(id) else expandedIds.add(id)
        }
    )
}

@Composable
fun DemoAppThemeRegion(
    theme: DemoTheme,
    dynamicTheme: Boolean,
    isExpanded: Boolean,
    onExpandedChanged: (expanded: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    CollapsibleRegion("App Theme", isExpanded = isExpanded, onExpandedChanged = onExpandedChanged) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Theme", modifier = Modifier.weight(1f))
            SegmentedButtons(
                items = DemoTheme.values().map { it.name },
                selectedIndex = theme.ordinal
            ) {
                scope.launch(Dispatchers.IO) {
                    AppPrefs.theme.update(DemoTheme.values()[it])
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Dynamic Theme", modifier = Modifier.weight(1f))
            SegmentedButtons(
                items = listOf("Yes", "No"),
                selectedIndex = if (dynamicTheme) 0 else 1
            ) {
                scope.launch(Dispatchers.IO) {
                    AppPrefs.dynamicTheme.update(it == 0)
                }
            }
        }
    }
}