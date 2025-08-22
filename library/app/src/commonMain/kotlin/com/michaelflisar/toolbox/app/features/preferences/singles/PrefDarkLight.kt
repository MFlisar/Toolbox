package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.michaelflisar.toolbox.app.features.preferences.BaseThemes
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.collectBaseTheme

@Composable
fun PreferenceScope.PrefDarkLight() {
    val baseThemes = Preferences.BaseThemes
    val value = Preferences.collectBaseTheme()
    PreferenceList(
        style = PreferenceList.Style.SegmentedButtons,
        value = value,
        items = baseThemes,
        itemTextProvider = { it.text },
        itemIconProvider = { Icon(it.icon, null) },
        title = ""
    )
}