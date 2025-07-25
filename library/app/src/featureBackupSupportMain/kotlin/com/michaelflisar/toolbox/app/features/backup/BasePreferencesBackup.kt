package com.michaelflisar.toolbox.app.features.backup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceSection
import com.michaelflisar.composepreferences.core.classes.asDependency
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.backup.BackupDefaults
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.backup.ui.BackupDialog
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_create_backup
import com.michaelflisar.toolbox.core.resources.settings_group_auto_backup
import com.michaelflisar.toolbox.core.resources.settings_group_backup
import com.michaelflisar.toolbox.core.resources.settings_restore_backup
import com.michaelflisar.toolbox.zip.interfaces.IZipContent
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun  PreferenceGroupScope.BasePreferencesBackup(
    backupSupport: IBackupSupport,
    appName: String,
    formatPath: (String) -> String?,
    onFolderForAutoBackupSelected: (directory: PlatformFile) -> String,
    onDisableAutoBackup: (path: String) -> Unit,
) {

    val backupDialog = rememberDialogState<BackupDialog.Mode>(null)
    val scope = rememberCoroutineScope()
    val backup = remember { backupSupport }

    PreferenceSection(title = stringResource(Res.string.settings_group_backup)) {
        PreferenceButton(
            title = stringResource(Res.string.settings_create_backup),
            icon = { Icon(Icons.Default.CloudUpload, null) },
            onClick = { backupDialog.show(BackupDialog.Mode.Export) },
        )
        PreferenceButton(
            title = stringResource(Res.string.settings_restore_backup),
            icon = { Icon(Icons.Default.CloudDownload, null) },
            onClick = { backupDialog.show(BackupDialog.Mode.Import) }
        )
    }
    if (backup.autoBackup) {
        val filePickerLauncher = rememberDirectoryPickerLauncher { directory ->
            // Handle the picked files
            if (directory != null) {
                val value = onFolderForAutoBackupSelected(directory)
                scope.launch(Platform.DispatcherIO) {
                    backupSupport.prefBackupPath.update(value)
                }
            }
        }

        PreferenceSection(
            title = stringResource(Res.string.settings_group_auto_backup),
            //icon = { Icon(Icons.Default.Schedule, null) }
        ) {
            val backupPath = backupSupport.prefBackupPath.asMutableStateNotNull()
            val backupDependencyEnabled = backupPath.asDependency { it.isNotEmpty() }
            val backupDependencyDisabled = backupPath.asDependency { it.isEmpty() }

            PreferenceInfo(
                title = "Automatische Backups",
                subtitle = "Du kannst automatische Backups aktivieren, um deine Daten zu sichern.",
                //icon = { Icon(Icons.Default.Info, null) },
                visible = backupDependencyDisabled,
                // TODO: Styling als Item welches nicht Teil der ModernStyle Region ist!
                //itemStyle = LocalPreferenceSettings.current.style.defaultItemStyle.copy(
                //    colors = PreferenceItemDefaults.colors(
                //        containerColor = MaterialTheme.colorScheme.primary,
                //        contentColor = MaterialTheme.colorScheme.onPrimary
                //    )
                //)
            )
            if (backupPath.value.isEmpty()) {
                PreferenceButton(
                    title = "Automatisches Backup aktivieren",
                    icon = {
                        Icon(
                            Icons.Default.Backup,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    subtitle = "Wähle einen Ordner für die automatischen Backups aus",
                    //enabled = backupDependencyEnabled,
                    onClick = {
                        filePickerLauncher.launch()
                    }
                )
            } else {
                PreferenceButton(
                    title = "Backup Ordner",
                    icon = { Icon(Icons.Default.Folder, null) },
                    subtitle = formatPath(backupPath.value),
                    onClick = {
                        filePickerLauncher.launch()
                    }
                )
            }

            // TODO: frequenz oder doch nur event basierend (nach jedem Training, ...)
            PreferenceButton(
                title = "Frequenz",
                icon = { Icon(Icons.Default.Repeat, null) },
                visible = backupDependencyEnabled,
                onClick = {
                    // TODO
                }
            )

            PreferenceButton(
                title = "Automatisches Backup deaktivieren",
                icon = { Icon(Icons.Default.Cancel, null) },
                visible = backupDependencyEnabled,
                onClick = {
                    scope.launch(Platform.DispatcherIO) {
                        onDisableAutoBackup(backupPath.value)
                        backupSupport.prefBackupPath.update("")
                    }
                }
            )

        }
    }

    BackupDialog(
        dialogState = backupDialog,
        exportFileName = {
            BackupDefaults.getDefaultBackupFileName(
                appName,
                backup.extension
            )
        },
        files = backup.backupContent,
        backupManager = BackupManager
    )

}