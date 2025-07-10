package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.toolbox.app.features.preferences.BaseAppPreferences
import com.michaelflisar.toolbox.app.features.preferences.groups.PreferenceSettingsTheme

object PageSettingsScreen : Screen {
    @Composable
    override fun Content() {
        PageSettings()
    }
}

@Composable
private fun PageSettings(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            //.padding(all = 16.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseAppPreferences(
            showProVersionDialog = rememberDialogState()
        ) {
            PreferenceSettingsTheme(true)
        }
    }
}