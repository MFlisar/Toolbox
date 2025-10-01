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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.michaelflisar.composedialogs.core.DialogButton
import com.michaelflisar.composedialogs.core.DialogButtonType
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composedialogs.core.stringOk
import com.michaelflisar.composedialogs.dialogs.frequency.DialogFrequency
import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency
import com.michaelflisar.composedialogs.dialogs.frequency.rememberDialogFrequency
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceSection
import com.michaelflisar.composepreferences.core.classes.asDependency
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.filekit.LocalFileKitDialogSettingsState
import com.michaelflisar.toolbox.backup.BackupDefaults
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.backup.ui.BackupDialog
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_create_backup
import com.michaelflisar.toolbox.core.resources.settings_group_auto_backup
import com.michaelflisar.toolbox.core.resources.settings_group_backup
import com.michaelflisar.toolbox.core.resources.settings_restore_backup
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.bookmarkData
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.fromBookmarkData
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceGroupScope.PreferencesBackup(
    formatPath: (String) -> String?,
) {
    val setup = CommonApp.setup
    val appName = setup.name()
    val backupDialog = rememberDialogState<BackupDialog.Mode>(null)
    val autoBackupFrequencyDialog = rememberDialogState()
    val scope = rememberCoroutineScope()
    val backup = BackupManager.manager!!
    val appState = LocalAppState.current

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
    if (backup.autoBackupConfig != null) {
        val filePickerLauncher = rememberDirectoryPickerLauncher { directory ->
            // Handle the picked files
            if (directory != null) {
                scope.launch(Platform.DispatcherIO) {
                    val bookmarkData = directory.bookmarkData()
                    setup.prefs.backupPathData.update(bookmarkData.bytes.toString(Charsets.UTF_8))
                }
            }
        }

        PreferenceSection(
            title = stringResource(Res.string.settings_group_auto_backup),
            //icon = { Icon(Icons.Default.Schedule, null) }
        ) {
            val backupPathData = setup.prefs.backupPathData.asMutableStateNotNull()
            val autoBackupFrequency = setup.prefs.autoBackupFrequency.asMutableStateNotNull()
            val backupDependencyEnabled = backupPathData.asDependency { it.isNotEmpty() }
            val backupDependencyDisabled = backupPathData.asDependency { it.isEmpty() }

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
            if (backupPathData.value.isEmpty()) {
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
                val platformFile =
                    PlatformFile.fromBookmarkData(backupPathData.value.toByteArray(Charsets.UTF_8))
                PreferenceButton(
                    title = "Backup Ordner",
                    icon = { Icon(Icons.Default.Folder, null) },
                    subtitle = formatPath(platformFile.path),
                    onClick = {
                        filePickerLauncher.launch()
                    }
                )
            }

            // TODO: frequenz oder doch nur event basierend (nach jedem Training, ...)
            PreferenceButton(
                title = "Frequenz",
                subtitle = autoBackupFrequency.value,
                icon = { Icon(Icons.Default.Repeat, null) },
                visible = backupDependencyEnabled,
                onClick = {
                    autoBackupFrequencyDialog.show()
                }
            )

            PreferenceButton(
                title = "Automatisches Backup deaktivieren",
                icon = { Icon(Icons.Default.Cancel, null) },
                visible = backupDependencyEnabled,
                onClick = {
                    scope.launch(Platform.DispatcherIO) {
                        // TODO: FileKit does not support this, does it? not that important though...
                        //context.contentResolver.releasePersistableUriPermission(uri, takeFlags)
                        // onDisableAutoBackup(backupPathData.value.toByteArray(Charsets.UTF_8))
                        setup.prefs.backupPathData.update("")
                        BackupManager.manager?.onSettingsChanged()
                    }
                }
            )

        }
    }

    BackupDialog(
        dialogState = backupDialog,
        exportFileName = BackupDefaults.getDefaultBackupFileName(
            appName,
            backup.config.extension,
            false
        ),
        files = backup.config.backupContent,
        backupManager = BackupManager.manager,
        fileKitDialogSettings = LocalFileKitDialogSettingsState.current
    )

    if (autoBackupFrequencyDialog.visible) {
        val autoBackupFrequency = setup.prefs.autoBackupFrequency.asMutableStateNotNull()
        val frequency = rememberDialogFrequency(
            if (autoBackupFrequency.value.isEmpty()) {
                Frequency.Weekly(DayOfWeek.SUNDAY, LocalTime(22, 0), 1)
            } else {
                Frequency.deserialize(autoBackupFrequency.value)
            }
        )
        DialogFrequency(
            state = autoBackupFrequencyDialog,
            frequency = frequency,
            title = { Text("Frequenz") },
            buttons = DialogDefaults.buttons(
                positive = DialogButton(stringOk()),
                //negative = DialogButton("DISABLE")
            ),
            onEvent = {
                println("onEvent: $it")
                if (it is DialogEvent.Button) {
                    val dataToSave = when (it.button) {
                        DialogButtonType.Positive -> frequency.value.serialize()
                        DialogButtonType.Negative -> ""
                    }
                    autoBackupFrequency.value = dataToSave
                    BackupManager.manager?.onSettingsChanged()
                }
            }
        )
    }

}