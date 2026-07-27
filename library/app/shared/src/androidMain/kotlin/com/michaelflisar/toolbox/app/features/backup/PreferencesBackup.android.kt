package com.michaelflisar.toolbox.app.features.backup

import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope

@Composable
internal actual fun PreferenceGroupScope.PreferencesBackup() {
    PreferencesBackup(
        formatPath = { it.toUri().toString() }
    )
}