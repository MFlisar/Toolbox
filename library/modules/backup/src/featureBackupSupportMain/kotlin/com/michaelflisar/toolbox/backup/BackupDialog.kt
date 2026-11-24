package com.michaelflisar.toolbox.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.composedialogs.styled.DialogInfo
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.components.StyledLinearProgressIndicator
import com.michaelflisar.composestyled.ui.layout.StyledDialog
import com.michaelflisar.composestyled.ui.layout.StyledDialogDefaults
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.state.DataDialogState
import com.michaelflisar.composestyled.ui.state.rememberDialogState
import com.michaelflisar.kmp.platformcontext.PlatformIO
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.backup.classes.BackupFileName
import com.michaelflisar.toolbox.backup.classes.BackupInspection
import com.michaelflisar.toolbox.backup.classes.BackupMetadata
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.backup_done
import com.michaelflisar.toolbox.core.resources.backup_error
import com.michaelflisar.toolbox.core.resources.backup_importing
import com.michaelflisar.toolbox.core.resources.backup_inspection_app
import com.michaelflisar.toolbox.core.resources.backup_inspection_app_version
import com.michaelflisar.toolbox.core.resources.backup_inspection_compatible
import com.michaelflisar.toolbox.core.resources.backup_inspection_created_at
import com.michaelflisar.toolbox.core.resources.backup_inspection_data_version
import com.michaelflisar.toolbox.core.resources.backup_inspection_legacy
import com.michaelflisar.toolbox.core.resources.backup_inspection_metadata_version
import com.michaelflisar.toolbox.core.resources.backup_inspection_validation_failed
import com.michaelflisar.toolbox.core.resources.backup_inspection_verifying
import com.michaelflisar.toolbox.core.resources.backup_restore
import com.michaelflisar.toolbox.core.resources.backup_select_another_file
import com.michaelflisar.toolbox.core.resources.backup_select_file
import com.michaelflisar.toolbox.core.resources.backup_waiting_for_file
import com.michaelflisar.toolbox.core.resources.backup_dialog_create_document_activity_not_found_info
import com.michaelflisar.toolbox.core.resources.backup_dialog_create_document_activity_not_found_title
import com.michaelflisar.toolbox.core.resources.backup_dialog_open_document_activity_not_found_info
import com.michaelflisar.toolbox.core.resources.backup_dialog_open_document_activity_not_found_title
import com.michaelflisar.toolbox.core.resources.backup_exporting
import com.michaelflisar.toolbox.core.resources.button_ok
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private sealed class State {

    @Composable
    abstract fun info(): String?

    data object Waiting : State() {
        @Composable
        override fun info() = stringResource(Res.string.backup_waiting_for_file)
    }

    class Running(val message: StringResource) : State() {
        @Composable
        override fun info() = stringResource(message)
    }

    class Inspected(
        val file: PlatformFile,
        val inspection: BackupInspection,
    ) : State() {
        @Composable
        override fun info(): String? = null
    }

    data object Done : State() {
        @Composable
        override fun info() = stringResource(Res.string.backup_done)
    }

    class Error(val error: Throwable) : State() {
        @Composable
        override fun info() =
            stringResource(Res.string.backup_error, error.message ?: error.toString())
    }
}

private sealed class Operation {
    class Export(val file: PlatformFile) : Operation()
    class Inspect(val file: PlatformFile) : Operation()
    class Restore(val file: PlatformFile) : Operation()
}

private enum class ActivityNotFoundError {
    Create, Open
}

@Composable
actual fun BackupDialog(
    dialogState: DataDialogState<BackupDialog.Mode>,
    exportFileName: BackupFileName,
    files: List<ZipFileContent>,
    backupManager: BackupManagerImpl?,
    fileKitDialogSettings: FileKitDialogSettings,
    extension: String,
) {
    if (dialogState.visible) {
        val mode = dialogState.requireData()
        val state = remember { mutableStateOf<State>(State.Waiting) }
        val operation = remember { mutableStateOf<Operation?>(null) }

        LaunchedEffect(dialogState.visible, mode) {
            state.value = State.Waiting
            operation.value = null
        }

        LaunchedEffect(Unit) {
            snapshotFlow { operation.value }
                .filterNotNull()
                .collect { request ->
                    operation.value = null
                    try {
                        when (request) {
                            is Operation.Export -> {
                                state.value = State.Running(Res.string.backup_exporting)
                                val manager = backupManager
                                    ?: error("Backup manager is not initialized")
                                val error = withContext(Dispatchers.PlatformIO) {
                                    manager.backup(files, request.file)
                                }
                                state.value = if (error == null) State.Done else State.Error(error)
                            }

                            is Operation.Inspect -> {
                                state.value = State.Running(Res.string.backup_inspection_verifying)
                                val manager = backupManager
                                    ?: error("Backup manager is not initialized")
                                val inspection = withContext(Dispatchers.PlatformIO) {
                                    manager.inspectBackup(request.file)
                                }
                                state.value = State.Inspected(request.file, inspection)
                            }

                            is Operation.Restore -> {
                                state.value = State.Running(Res.string.backup_importing)
                                val manager = backupManager
                                    ?: error("Backup manager is not initialized")
                                val error = withContext(Dispatchers.PlatformIO) {
                                    manager.restore(files, request.file)
                                }
                                state.value = if (error == null) State.Done else State.Error(error)
                            }
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        L.e(e)
                        state.value = State.Error(e)
                    }
                }
        }

        val onFileSelected: (PlatformFile?) -> Unit = { file ->
            if (file != null) {
                operation.value = if (mode == BackupDialog.Mode.Export) {
                    Operation.Export(file)
                } else {
                    Operation.Inspect(file)
                }
            }
        }

        val createFilePicker = rememberFileSaverLauncher(fileKitDialogSettings) {
            onFileSelected(it)
        }
        val openFilePicker = rememberFilePickerLauncher(
            type = FileKitType.File(extensions = listOf(extension)),
            dialogSettings = fileKitDialogSettings
        ) {
            onFileSelected(it)
        }
        val dialogActivityNotFound = rememberDialogState<ActivityNotFoundError>(null)

        LaunchedEffect(mode) {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupDialog: opened, mode=$mode, files=${files.size}, managerAvailable=${backupManager != null}"
            }
        }

        val currentState = state.value
        val inspected = currentState as? State.Inspected
        val canRestore = inspected?.inspection?.isCompatible == true

        StyledDialog(
            state = dialogState,
            title = { StyledText(stringResource(mode.title)) },
            buttons = StyledDialog.Buttons(
                positive = StyledDialog.Button(
                    label = stringResource(Res.string.button_ok),
                    onClick = {
                        if (state.value is State.Done && mode == BackupDialog.Mode.Import) {
                            backupManager?.onBackupRestored()
                        }
                    },
                    enabled = inspected != null || currentState is State.Done || currentState is State.Error
                )
            ),
            content = StyledDialogDefaults.content {
                Column(
                    verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (inspected != null) {
                        BackupInspectionDetails(inspected.inspection)
                    }

                    if (canRestore) {
                        StyledButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                inspected?.let { operation.value = Operation.Restore(it.file) }
                            }
                        ) {
                            StyledText(stringResource(Res.string.backup_restore))
                        }
                    }

                    if (currentState is State.Waiting || inspected != null || currentState is State.Error) {
                        StyledButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                when (mode) {
                                    BackupDialog.Mode.Export -> {
                                        try {
                                            createFilePicker.launch(
                                                suggestedName = exportFileName.nameWithoutExtension,
                                                defaultExtension = exportFileName.extension
                                            )
                                        } catch (e: ActivityNotFoundException) {
                                            dialogActivityNotFound.show(ActivityNotFoundError.Create)
                                        }
                                    }

                                    BackupDialog.Mode.Import -> {
                                        try {
                                            openFilePicker.launch()
                                        } catch (e: ActivityNotFoundException) {
                                            dialogActivityNotFound.show(ActivityNotFoundError.Open)
                                        }
                                    }
                                }
                            }
                        ) {
                            val label = if (inspected != null || currentState is State.Error) {
                                Res.string.backup_select_another_file
                            } else {
                                Res.string.backup_select_file
                            }
                            StyledText(stringResource(label))
                        }
                    }

                    if (currentState is State.Running) {
                        StyledLinearProgressIndicator()
                    }
                    val info = currentState.info()
                    if (info != null) {
                        StyledText(info)
                    }
                }
            }
        )

        if (dialogActivityNotFound.visible) {
            DialogBackupActivityNotFound(dialogActivityNotFound)
        }
    }
}

@Composable
private fun BackupInspectionDetails(inspection: BackupInspection) {
    val metadata = inspection.metadata
    if (metadata == null) {
        StyledText(stringResource(Res.string.backup_inspection_legacy))
        if (!inspection.isValid) {
            StyledText(stringResource(Res.string.backup_inspection_validation_failed))
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium)
    ) {
        StyledText(stringResource(Res.string.backup_inspection_app, metadata.appId))
        StyledText(
            stringResource(
                Res.string.backup_inspection_app_version,
                metadata.appVersion ?: "-"
            )
        )
        StyledText(
            stringResource(
                Res.string.backup_inspection_data_version,
                metadata.dataVersion
            )
        )
        StyledText(
            stringResource(
                Res.string.backup_inspection_created_at,
                metadata.createdAt ?: "-"
            )
        )
        StyledText(
            stringResource(
                Res.string.backup_inspection_metadata_version,
                metadata.metadataVersion
            )
        )
        metadata.custom.toSortedMap().forEach { (key, value) ->
            StyledText("$key: $value")
        }
    }

    if (!inspection.isValid) {
        StyledText(stringResource(Res.string.backup_inspection_validation_failed))
    } else {
        StyledText(stringResource(Res.string.backup_inspection_compatible))
    }
}

@Composable
private fun DialogBackupActivityNotFound(
    dialogState: DataDialogState<ActivityNotFoundError>,
) {
    val data = dialogState.requireData()
    val title = when (data) {
        ActivityNotFoundError.Create -> Res.string.backup_dialog_create_document_activity_not_found_title
        ActivityNotFoundError.Open -> Res.string.backup_dialog_open_document_activity_not_found_title
    }
    val info = when (data) {
        ActivityNotFoundError.Create -> Res.string.backup_dialog_create_document_activity_not_found_info
        ActivityNotFoundError.Open -> Res.string.backup_dialog_open_document_activity_not_found_info
    }
    DialogInfo(
        state = dialogState,
        title = { StyledText(stringResource(title)) },
        info = stringResource(info)
    )
}
