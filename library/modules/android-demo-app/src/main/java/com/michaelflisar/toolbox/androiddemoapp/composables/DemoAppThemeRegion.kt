package com.michaelflisar.toolbox.androiddemoapp.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.androiddemoapp.classes.DemoPrefs
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MySwitch
import com.michaelflisar.toolbox.components.MyThemeSwitcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DemoAppThemeRegion(
    regionId: Int,
    state: DemoCollapsibleRegion.State,
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
        MyColumn {
            MyThemeSwitcher(
                modifier = Modifier.fillMaxWidth(),
                dark = ComposeTheme.BaseTheme.Dark,
                light = ComposeTheme.BaseTheme.Light,
                system = ComposeTheme.BaseTheme.System,
                mapper = { it.name },
                selected = baseTheme
            ) { item ->
                scope.launch(Dispatchers.IO) {
                    DemoPrefs.baseTheme.update(item)
                }
            }
            val themes by remember {
                derivedStateOf {
                    ComposeTheme.getRegisteredThemes().sortedBy { it.key.lowercase() }
                }
            }
            AnimatedVisibility(visible = dynamic) {
                Text("The color scheme is derived from the wallpaper if dynamic theme is enabled!")
            }
            AnimatedVisibility(visible = !dynamic) {
                DemoDropdown(
                    label = "Color Scheme",
                    modifier = Modifier.fillMaxWidth(),
                    items = themes,
                    selected = themes.find { it.key == themeKey }!!,
                    itemToString = { item, dropdown -> item.key },
                    leadingIcon = { item, dropdown ->
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (baseTheme.isDark()) item.colorSchemeDark.primary else item.colorSchemeLight.primary)
                        )
                    },
                    onItemSelected = { index, item ->
                        scope.launch(Dispatchers.IO) {
                            DemoPrefs.themeKey.update(item.key)
                        }
                    }
                )
            }
            MySwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Dynamic Theme",
                checked = dynamic,
                onCheckedChange = {
                    scope.launch(Dispatchers.IO) {
                        DemoPrefs.dynamic.update(it)
                    }
                }
            )
        }
    }
}

@Composable
fun DemoAppThemeRegionDetailed(
    state: DemoCollapsibleRegion.State
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
            Text(
                "${ComposeTheme.getRegisteredThemes().size} themes available",
                style = MaterialTheme.typography.labelSmall
            )
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