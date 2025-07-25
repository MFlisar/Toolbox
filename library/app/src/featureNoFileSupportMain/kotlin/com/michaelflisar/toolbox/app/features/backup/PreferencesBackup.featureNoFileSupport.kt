package com.michaelflisar.toolbox.app.features.backup

import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope

@Composable
internal actual fun PreferenceGroupScope.PreferencesBackup(
    backupSupport: IBackupSupport,
    appName: String,
) {
    // No file support in this build variant
    throw UnsupportedOperationException("File support is not available in this build variant")
}