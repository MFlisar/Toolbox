package com.michaelflisar.toolbox.demo.pages

import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.pages.PageSettings

@Parcelize
object PageSettingsScreen : PageSettings() {

    override val addThemeSetting: Boolean = true

    @Composable
    override fun PreferenceGroupScope.CustomContent() {
        // --
    }
}