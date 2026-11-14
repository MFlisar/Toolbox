package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composethemer.picker.ThemePicker
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_dynamic_theme
import com.michaelflisar.toolbox.core.resources.settings_dynamic_theme_details
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceScope.PrefDynamicTheme(
    pickerState: ThemePicker.State,
    showIcon: Boolean
) {
    PreferenceBool(
        value = pickerState.dynamic,
        style = PreferenceBool.Style.Checkbox,
        title = stringResource(Res.string.settings_dynamic_theme),
        subtitle = stringResource(Res.string.settings_dynamic_theme_details),
        icon = if (showIcon) {
            {
                Icon(
                    Icons.Outlined.FormatPaint,
                    contentDescription = null
                )
            }
        } else null
    )
}