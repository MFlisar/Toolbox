package com.michaelflisar.toolbox.app.features.backup

import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope

@Composable
internal actual fun PreferenceGroupScope.PreferencesBackup(
    backupSupport: IBackupSupport,
    appName: String,
) {
    BasePreferencesBackup(
        backupSupport = backupSupport,
        appName = appName,
        formatPath = { it },
        onFolderForAutoBackupSelected = { directory ->
            directory.file.absolutePath
        },
        onDisableAutoBackup = { path ->
            // --
        }
    )
}