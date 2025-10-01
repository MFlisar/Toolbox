package com.michaelflisar.toolbox.app.features.backup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.PreferenceSubScreen
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_group_backup
import org.jetbrains.compose.resources.stringResource

@Composable
internal expect fun PreferenceGroupScope.PreferencesBackup()

@Composable
fun PreferenceGroupScope.ContentPreferences() {
    PreferencesBackup()
}

@Composable
fun PreferenceScope.ContentPreferencesAsSubPreference() {
    PreferenceSubScreen(
        title = stringResource(Res.string.settings_group_backup),
        icon = { Icon(Icons.Default.CloudUpload, null) }
    ) {
        ContentPreferences()
    }
}