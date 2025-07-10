package com.michaelflisar.toolbox.app.features.preferences.groups

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.PreferenceSection
import com.michaelflisar.composepreferences.core.PreferenceSubScreen
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.core.scopes.PreferenceSectionScope
import com.michaelflisar.composethemer.picker.rememberMultiLevelThemePicker
import com.michaelflisar.composethemer.picker.rememberThemePicker
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.features.preferences.singles.PrefContrast
import com.michaelflisar.toolbox.app.features.preferences.singles.PrefDarkLight
import com.michaelflisar.toolbox.app.features.preferences.singles.PrefDynamicTheme
import com.michaelflisar.toolbox.app.features.preferences.singles.PrefTheme
import com.michaelflisar.toolbox.app.features.preferences.singles.PrefToolbarStyle
import com.michaelflisar.toolbox.app.resources.Res
import com.michaelflisar.toolbox.app.resources.settings_group_style
import com.michaelflisar.toolbox.app.resources.settings_theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceGroupScope.PreferenceSettingsTheme(
    wrapInSection: Boolean,
) {
    val themeSupport = CommonApp.setup.themeSupport
    if (themeSupport.supportsTheming()) {
        if (wrapInSection) {
            PreferenceSection(
                title = stringResource(Res.string.settings_group_style)
            ) {
                PreferenceSettingsThemeSubScreen()
            }
        } else {
            PreferenceSettingsThemeSubScreen()
        }
    }
}

@Composable
fun PreferenceSectionScope.PreferenceSettingsTheme() {
    val themeSupport = CommonApp.setup.themeSupport
    if (themeSupport.supportsTheming()) {
        PreferenceSettingsThemeSubScreen()
    }
}

@Composable
private fun PreferenceScope.PreferenceSettingsThemeSubScreen() {

    val setup = CommonApp.setup
    val themeSupport = setup.themeSupport

    if (!themeSupport.supportsTheming()) {
        return
    }

    val supportCustomTheme = themeSupport.supportsCustomThemes()
    val isSystemContrastSupported =
        false // isContrastAvailable() // TODO: themer sollte das für alle Platformen machen!

    //val themeState = LocalThemeState.current

    val pickerState = rememberThemePicker(
        baseTheme = setup.prefs.theme.asMutableStateNotNull(),
        contrast = setup.prefs.contrast.asMutableStateNotNull(),
        dynamic = setup.prefs.dynamicTheme.asMutableStateNotNull(),
        themeId = setup.prefs.customTheme.asMutableStateNotNull()
    )

    PreferenceSubScreen(
        title = stringResource(Res.string.settings_theme),
        icon = { Icon(Icons.Outlined.Style, contentDescription = null) }
    ) {
        PreferenceSection(
            title = stringResource(Res.string.settings_theme)
        ) {
            // 1) Base Theme (dark/light/system)
            if (themeSupport.supportDarkLight) {
                PrefDarkLight(pickerState)
            }

            // 2) Toolbar Style
            if (themeSupport.supportToolbarStyles) {
                PrefToolbarStyle()
            }

            // 3) Dynamic Colors
            if (themeSupport.supportDynamicColors) {
                PrefDynamicTheme(pickerState)
            }

            // 4) Contrast
            if (themeSupport.supportContrast) {
                PrefContrast(pickerState, isSystemContrastSupported)
            }

            // 5) Theme
            if (supportCustomTheme) {
                val multiLevelState = rememberMultiLevelThemePicker(pickerState)
                PrefTheme(pickerState, multiLevelState)
            }
        }
    }
}