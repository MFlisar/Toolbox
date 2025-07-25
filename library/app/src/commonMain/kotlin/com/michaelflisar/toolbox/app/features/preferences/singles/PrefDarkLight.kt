package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.features.preferences.BaseThemes
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.collectBaseTheme
import com.michaelflisar.toolbox.app.platform.AppPrefs

/*
@Composable
fun PreferenceScope.PrefDarkLight(
    pickerState: ThemePicker.State,
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
*/
@Composable
fun PreferenceScope.PrefDarkLight(
    prefs: AppPrefs,
) {
    val baseThemes = Preferences.BaseThemes
    val value = Preferences.collectBaseTheme(prefs)
    PreferenceList(
        style = PreferenceList.Style.SegmentedButtons,
        value = value,
        items = baseThemes,
        itemTextProvider = { it.text },
        itemIconProvider = { Icon(it.icon, null) },
        title = ""
    )
}