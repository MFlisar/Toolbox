package com.michaelflisar.toolbox.demo.pages

import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.preferences.AppPreferencesDefaults
import com.michaelflisar.toolbox.app.features.preferences.AppPreferencesStyle
import com.michaelflisar.toolbox.app.pages.PageSettings

@Parcelize
object PageSettingsScreen : PageSettings() {

    @Composable
    override fun provideStyle(): AppPreferencesStyle {
        return AppPreferencesDefaults.styleDeviceDefault(
            addThemeSettings = true,
            customContent = { CustomContent() }
        )
    }

    @Composable
    private fun PreferenceGroupScope.CustomContent() {
        // ...
        PreferenceInfo(title = "Test")
    }
}