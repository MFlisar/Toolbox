package com.michaelflisar.toolbox.demo.pages

import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.preferences.AppPreferencesDefaults
import com.michaelflisar.toolbox.app.features.preferences.AppPreferencesStyle
import com.michaelflisar.toolbox.app.pages.PageSettings

@Parcelize
object PageSettingsScreen : PageSettings() {

    override val style: AppPreferencesStyle
        @Composable get() = AppPreferencesDefaults.stylePager(
            addThemeSettings = true,
            customPages = listOf(
                AppPreferencesStyle.Pager.Page("Custom") { CustomContent() }
            )
        )

    @Composable
    private fun PreferenceGroupScope.CustomContent() {
        // ..
    }
}