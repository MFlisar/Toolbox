package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.composepreferences.core.PreferenceCustom
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.picker.ThemePicker
import com.michaelflisar.composethemer.picker.composables.BaseThemePicker
import com.michaelflisar.toolbox.app.resources.Res
import com.michaelflisar.toolbox.app.resources.theme_dark
import com.michaelflisar.toolbox.app.resources.theme_light
import com.michaelflisar.toolbox.app.resources.theme_system
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceScope.PrefDarkLight(
    pickerState: ThemePicker.State
) {
    PreferenceCustom {
        val hideIconForSelectedItem = true
        BaseThemePicker(
            modifier = Modifier.fillMaxWidth(),
            state = pickerState
        ) { item, data ->
            val text = when (item) {
                ComposeTheme.BaseTheme.Dark -> stringResource(Res.string.theme_dark)
                ComposeTheme.BaseTheme.Light -> stringResource(Res.string.theme_light)
                ComposeTheme.BaseTheme.System -> stringResource(Res.string.theme_system)
                null -> null
            }
            val icon = when (item) {
                ComposeTheme.BaseTheme.System -> Icons.Default.PhoneAndroid
                ComposeTheme.BaseTheme.Dark -> Icons.Default.DarkMode
                ComposeTheme.BaseTheme.Light -> Icons.Default.LightMode
                null -> null
            }.takeIf { !hideIconForSelectedItem || !data.selected }
            PrefPickerRow(data, icon, text)
        }
    }
}