package com.michaelflisar.toolbox.app.features.backup

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.toolbox.zip.interfaces.IZipContent
import io.github.vinceglb.filekit.dialogs.uri

@Composable
actual fun PreferenceGroupScope.PreferencesBackup(
    backupSupport: IBackupSupport,
    appName: String
) {

    val context = LocalContext.current
    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    BasePreferencesBackup(
        backupSupport = backupSupport,
        appName = appName,
        formatPath = { it.toUri().path },
        onFolderForAutoBackupSelected = { directory ->
            val uri = directory.uri
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            uri.toString()
        },
        onDisableAutoBackup = { path ->
            val uri = path.toUri()
            context.contentResolver.releasePersistableUriPermission(uri, takeFlags)
        }
    )
}