package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.michaelflisar.toolbox.app.features.theme.ThemeSetup

@Composable
fun PreferenceScope.PrefDarkLight(
    showText: Boolean
) {
    val setup = ThemeSetup.get()
    val baseThemes = setup.baseThemes
    val value = setup.collectBaseTheme()
    PreferenceList(
        style = PreferenceList.Style.SegmentedButtons,
        value = value,
        items = baseThemes,
        itemTextProvider = { if (showText) setup.getText(it) else "" },
        itemIconProvider = { Icon(setup.getIcon(it), null) },
        title = ""
    )
}