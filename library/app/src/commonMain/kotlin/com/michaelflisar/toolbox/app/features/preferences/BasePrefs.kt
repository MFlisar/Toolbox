package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency
import com.michaelflisar.composestyled.StyledThemeConfig
import com.michaelflisar.composestyled.classes.StyledThemeProvider
import com.michaelflisar.composestyled.themes.StyledThemeAppearance
import com.michaelflisar.composestyled.themes.StyledThemeColors
import com.michaelflisar.composestyled.themes.StyledThemeContrast
import com.michaelflisar.composestyled.themes.StyledThemeVariant
import com.michaelflisar.kotpreferences.compose.collectAsStateWithLifecycleNotNull
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.theme.themeProvider
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

open class BasePrefs(
    storage: Storage,
    initialThemeColors: StyledThemeColors,
    initialThemeVariant: StyledThemeVariant = StyledThemeVariant.Colorful,
    initialAppearance: StyledThemeAppearance = StyledThemeAppearance.System,
    initialContrast: StyledThemeContrast = StyledThemeContrast.System,
    initialToolbarStyle: ToolbarStyle = ToolbarStyle.Primary,
    initialFrequency: Frequency = Frequency.Weekly(DayOfWeek.SUNDAY, LocalTime(22, 0), 1),
) : SettingsModel(storage) {

    // Theme
    val themeColors by stringPref(initialThemeColors.id)
    val themeVariant by enumPref(initialThemeVariant, StyledThemeVariant.entries)
    val themeAppearance by enumPref(initialAppearance, StyledThemeAppearance.entries)
    val themeContrast by enumPref(initialContrast, StyledThemeContrast.entries)

    // theme specials
    val dynamicTheme by boolPref(false)
    val toolbarStyle by enumPref(initialToolbarStyle, ToolbarStyle.entries)

    // Changelog
    val lastShownVersionForChangelog by longPref(-1L)

    // Version
    val lastAppVersion by longPref(-1L)

    // Backup
    val backupPathData by stringPref("")
    val autoBackupFrequency by stringPref(initialFrequency.serialize())
}

@Composable
fun rememberThemeConfig(provider: StyledThemeProvider = themeProvider()): StyledThemeConfig {

    val prefs = AppSetup.get().prefs

    val colorsId = prefs.themeColors.collectAsStateWithLifecycleNotNull()
    val variant = prefs.themeVariant.collectAsStateWithLifecycleNotNull()
    val appearance = prefs.themeAppearance.collectAsStateWithLifecycleNotNull()
    val contrast = prefs.themeContrast.collectAsStateWithLifecycleNotNull()
    val dynamic = prefs.dynamicTheme.collectAsStateWithLifecycleNotNull()

    val collections = AppSetup.get().themeSupport.colorsCollections
    val colors = remember(provider, collections, colorsId.value) {
        collections .flatMap { it.themes }.find {
            it.id == colorsId.value
        } ?: provider.defaultThemeColors
    }

    return StyledThemeConfig(
        colors = colors,
        variant = variant.value,
        appearance = appearance.value,
        contrast = contrast.value,
        dynamicColors = dynamic.value
    )
}