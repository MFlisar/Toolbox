package com.michaelflisar.toolbox.androiddemoapp.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.isContrastAvailable
import com.michaelflisar.composethemer.picker.DefaultThemePicker
import com.michaelflisar.composethemer.picker.DefaultThemePickerIconTextContent
import com.michaelflisar.composethemer.picker.rememberThemePicker
import com.michaelflisar.kotpreferences.compose.asMutableState
import com.michaelflisar.toolbox.androiddemoapp.classes.DemoPrefs
import com.michaelflisar.toolbox.components.MyColumn

@Composable
fun DemoAppThemeRegion(
    regionId: Int,
    state: DemoCollapsibleRegion.State,
    modifier: Modifier = Modifier,
    singleLevelThemePicker: Boolean = false
) {
    DemoCollapsibleRegion(
        modifier = modifier,
        title = "App Theme",
        regionId = regionId,
        state = state
    ) {
        MyColumn {
            ThemePicker(singleLevelThemePicker)
        }
    }
}

@Composable
private fun ThemePicker(singleLevelThemePicker: Boolean) {

    val pickerState = rememberThemePicker(
        baseTheme = DemoPrefs.baseTheme.asMutableState(),
        contrast = DemoPrefs.contrast.asMutableState(),
        dynamic = DemoPrefs.dynamic.asMutableState(),
        themeId = DemoPrefs.theme.asMutableState()
    )

    DefaultThemePicker(
        modifier = Modifier.fillMaxWidth(),
        baseTheme = pickerState.baseTheme,
        contrast = pickerState.contrast,
        dynamic = pickerState.dynamic,
        theme = pickerState.selectedThemeId,
        singleLevelThemePicker = singleLevelThemePicker,
        isSystemContrastSupported = isContrastAvailable(), // only android supports system contrast
        isDynamicColorsSupported = true, // only android supports dynamic colors
        labelWidth = 72.dp,
        //labelBaseTheme = "Base Theme",
        //labelContrast = "Contrast",
        //labelDynamic = "Dynamic Colors",
        //labelTheme = "Theme",
        imageVectorSystem = Icons.Default.PhoneAndroid,
        baseThemeContent = { item, data ->
            val icon = when (item) {
                ComposeTheme.BaseTheme.Dark -> Icons.Default.DarkMode
                ComposeTheme.BaseTheme.Light -> Icons.Default.LightMode
                ComposeTheme.BaseTheme.System -> Icons.Default.PhoneAndroid
                else -> null
            }
            val text = when (item) {
                ComposeTheme.BaseTheme.Dark -> null
                ComposeTheme.BaseTheme.Light -> null
                ComposeTheme.BaseTheme.System -> null//"System"
                else -> null
            }
            DefaultThemePickerIconTextContent(data, icon = icon, text = text)
        },
        contrastContent = { item, data ->
            val icon = when (item) {
                ComposeTheme.Contrast.Normal -> Icons.Default.BrightnessLow
                ComposeTheme.Contrast.Medium -> Icons.Default.BrightnessMedium
                ComposeTheme.Contrast.High -> Icons.Default.BrightnessHigh
                ComposeTheme.Contrast.System -> Icons.Default.PhoneAndroid
                else -> null
            }
            val text = when (item) {
                ComposeTheme.Contrast.Normal -> null
                ComposeTheme.Contrast.Medium -> null
                ComposeTheme.Contrast.High -> null
                ComposeTheme.Contrast.System -> null//"System"
                else -> null
            }
            DefaultThemePickerIconTextContent(data, icon = icon, text = text)
        }
    )
}