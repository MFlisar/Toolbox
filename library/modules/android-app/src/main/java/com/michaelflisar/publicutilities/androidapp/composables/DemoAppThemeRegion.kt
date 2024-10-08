package com.michaelflisar.publicutilities.androidapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.androidapp.classes.DemoPrefs
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.publicutilities.composables.DemoCollapsibleRegion
import com.michaelflisar.publicutilities.composables.DemoDropdown
import com.michaelflisar.publicutilities.composables.DemoSegmentedButtons
import com.michaelflisar.publicutilities.composables.ExpandedRegionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DemoAppThemeRegion(
    regionId: Int,
    state: ExpandedRegionState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val baseTheme by DemoPrefs.baseTheme.collectAsStateNotNull()
    val dynamic by DemoPrefs.dynamic.collectAsStateNotNull()
    val themeKey by DemoPrefs.themeKey.collectAsStateNotNull()

    DemoCollapsibleRegion(
        modifier = modifier,
        title = "App Theme",
        regionId = regionId,
        state = state
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.ColorLens, null)
            Text("Base Theme", modifier = Modifier.weight(1f))
            DemoSegmentedButtons(
                modifier = Modifier.weight(2f),
                items = ComposeTheme.BaseTheme.entries,
                itemToText = { it.name },
                initialSelectedIndex = ComposeTheme.BaseTheme.entries.indexOf(baseTheme)
            ) { _, item ->
                scope.launch(Dispatchers.IO) {
                    DemoPrefs.baseTheme.update(item)
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
                modifier = Modifier.weight(2f),
                items = ComposeTheme.getRegisteredThemes().map { it.key },
                selected = themeKey
            ) { _, item ->
                scope.launch(Dispatchers.IO) {
                    DemoPrefs.themeKey.update(item)
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
                modifier = Modifier.weight(2f),
                items = listOf("Yes", "No"),
                initialSelectedIndex = if (dynamic) 0 else 1
            ) {
                scope.launch(Dispatchers.IO) {
                    DemoPrefs.dynamic.update(it == 0)
                }
            }
        }
    }
}

@Composable
fun DemoAppThemeRegionDetailed(
    state: ExpandedRegionState
) {
    val scope = rememberCoroutineScope()
    val showLabels = rememberSaveable { mutableStateOf(true) }

    val baseTheme by DemoPrefs.baseTheme.collectAsStateNotNull()
    val dynamic by DemoPrefs.dynamic.collectAsStateNotNull()
    val themeKey by DemoPrefs.themeKey.collectAsStateNotNull()

    DemoCollapsibleRegion(
        title = "Theme - Color Scheme",
        regionId = -1,
        state = state
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${ComposeTheme.getRegisteredThemes().size} themes available", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.weight(1f))
            Text("Labels", style = MaterialTheme.typography.labelSmall)
            Checkbox(
                checked = showLabels.value,
                onCheckedChange = {
                    showLabels.value = it
                }
            )
        }


        if (dynamic) {
            Text(
                "If dynamic theme is enabled the selected theme won't have any impact as colors are derived dynamically from the current wallpaper!",
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ComposeTheme.getRegisteredThemes()
                    .forEach {
                        FilterChip(
                            selected = themeKey == it.key,
                            onClick = {
                                scope.launch {
                                    DemoPrefs.themeKey.update(it.key)
                                }
                            },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (showLabels.value) {
                                        Text(it.key)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(if (baseTheme.isDark()) it.colorSchemeDark.primary else it.colorSchemeLight.primary)
                                    )
                                }

                            },
                            leadingIcon = if (themeKey == it.key) {
                                {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            } else null
                        )
                    }
            }
            Text(
                "Selected Theme: $themeKey",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
    DemoCollapsibleRegion(
        title = "Theme - Style",
        regionId = -2,
        state = state
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.ColorLens, null)
            Text("Base Theme", modifier = Modifier.weight(1f))
            DemoSegmentedButtons(
                modifier = Modifier.weight(2f),
                items = ComposeTheme.BaseTheme.entries,
                itemToText = { it.name },
                initialSelectedIndex = ComposeTheme.BaseTheme.entries.indexOf(baseTheme),
                onItemSelected = { index, item ->
                    scope.launch {
                        DemoPrefs.baseTheme.update(item)
                    }
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.FormatPaint, null)
            Text("Dynamic Theme", modifier = Modifier.weight(1f))
            DemoSegmentedButtons(
                modifier = Modifier.weight(2f),
                items = listOf("Yes", "No"),
                initialSelectedIndex = if (dynamic) 0 else 1,
                onItemSelected = { index ->
                    scope.launch {
                        DemoPrefs.dynamic.update(index == 0)
                    }
                }
            )
        }
    }
}