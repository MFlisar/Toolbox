package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.composepreferences.core.classes.Dependency
import com.michaelflisar.composepreferences.core.composables.BasePreference
import com.michaelflisar.composepreferences.core.composables.PreferenceItemSetup
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.picker.ThemePicker
import com.michaelflisar.composethemer.picker.composables.ContrastPicker
import com.michaelflisar.composethemer.picker.internal.SingleChoice
import com.michaelflisar.toolbox.app.resources.Res
import com.michaelflisar.toolbox.app.resources.contrast_high
import com.michaelflisar.toolbox.app.resources.contrast_medium
import com.michaelflisar.toolbox.app.resources.contrast_normal
import com.michaelflisar.toolbox.app.resources.settings_contrast
import com.michaelflisar.toolbox.app.resources.theme_system
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceScope.PrefContrast(
    pickerState: ThemePicker.State,
    isSystemContrastSupported: Boolean
) {
    BasePreference(
        itemSetup = PreferenceItemSetup(
            contentPlacementBottom = true
        ),
        enabled = Dependency.State(pickerState.isContrastEnabled) { it },
        title = stringResource(Res.string.settings_contrast),
        icon = { Icon(Icons.Outlined.SettingsBrightness, contentDescription = null) }
    ) {
        ContrastPicker(
            modifier = Modifier.fillMaxWidth(),
            state = pickerState,
            isSystemContrastSupported = isSystemContrastSupported,
            style = SingleChoice.Style.Dropdown()
        ) { item, data ->
            val text = when (item) {
                ComposeTheme.Contrast.Normal -> stringResource(Res.string.contrast_normal)
                ComposeTheme.Contrast.Medium -> stringResource(Res.string.contrast_medium)
                ComposeTheme.Contrast.High -> stringResource(Res.string.contrast_high)
                ComposeTheme.Contrast.System -> stringResource(Res.string.theme_system)
                null -> null
            }
            val icon = when (item) {
                ComposeTheme.Contrast.Normal -> Icons.Default.BrightnessLow
                ComposeTheme.Contrast.Medium -> Icons.Default.BrightnessMedium
                ComposeTheme.Contrast.High -> Icons.Default.BrightnessHigh
                ComposeTheme.Contrast.System -> Icons.Default.PhoneAndroid
                null -> null
            }
            PrefPickerRow(data, icon, text)
        }
    }
}