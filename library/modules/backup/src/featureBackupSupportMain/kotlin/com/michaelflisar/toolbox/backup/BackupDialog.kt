package com.michaelflisar.toolbox.backup

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.michaelflisar.composedialogs.styled.DialogInfo
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.components.StyledLinearProgressIndicator
import com.michaelflisar.composestyled.ui.layout.StyledColumn
import com.michaelflisar.composestyled.ui.layout.StyledDialog
import com.michaelflisar.composestyled.ui.layout.StyledDialogDefaults
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.state.DataDialogState
import com.michaelflisar.composestyled.ui.state.rememberDialogState
import com.michaelflisar.kmp.platformcontext.PlatformIO
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.backup.classes.BackupFileName
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.backup_dialog_create_document_activity_not_found_info
import com.michaelflisar.toolbox.core.resources.backup_dialog_create_document_activity_not_found_title
import com.michaelflisar.toolbox.core.resources.backup_dialog_open_document_activity_not_found_info
import com.michaelflisar.toolbox.core.resources.backup_dialog_open_document_activity_not_found_title
import com.michaelflisar.toolbox.core.resources.backup_done
import com.michaelflisar.toolbox.core.resources.backup_error
import com.michaelflisar.toolbox.core.resources.backup_select_file
import com.michaelflisar.toolbox.core.resources.backup_waiting_for_file
import com.michaelflisar.toolbox.core.resources.button_ok
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private sealed class State {

    @Composable
    abstract fun info(): String

    data object Waiting : State() {
        @Composable
        override fun info() = stringResource(Res.string.backup_waiting_for_file)
    }

    class Running(val info: StringResource) : State() {
        @Composable
        override fun info() = stringResource(info)
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
        val selectedFile = remember { mutableStateOf<PlatformFile?>(null) }
        LaunchedEffect(Unit) {
            snapshotFlow { selectedFile.value }
                .collect { file ->
                    if (file != null) {
                    L.logIf { BackupDefaults.LOG }?.d {
                        "BackupDialog: file selected, mode=$mode"
                    }
                    state.value = State.Running(mode.running)
                    withContext(Dispatchers.PlatformIO) {
                        L.logIf { BackupDefaults.LOG }?.d {
                            "BackupDialog: starting operation, mode=$mode, managerAvailable=${backupManager != null}"
                        }
                        val exception = when (mode) {
                            BackupDialog.Mode.Export -> backupManager!!.backup(files, file)
                            BackupDialog.Mode.Import -> backupManager!!.restore(files, file)
                        }
                        L.logIf { BackupDefaults.LOG }?.d {
                            "BackupDialog: operation finished, mode=$mode, succeeded=${exception == null}"
                        }
                        state.value = if (exception == null) {
                                State.Done
                            } else State.Error(exception)
                        }
                    }
                }
        }

        // https://github.com/vinceglb/FileKit/issues/111
        // https://github.com/vinceglb/FileKit/blob/main/filekit-dialogs-compose/src/webMain/kotlin/io/github/vinceglb/filekit/dialogs/compose/FileKitCompose.web.kt#L16
        val createFilePicker = rememberFileSaverLauncher(fileKitDialogSettings) {
            selectedFile.value = it
        }
        val openFilePicker = rememberFilePickerLauncher(
            type = FileKitType.File(extensions = listOf(extension)),
            dialogSettings = fileKitDialogSettings
        ) {
            selectedFile.value = it
        }

        val dialogActivityNotFound = rememberDialogState<ActivityNotFoundError>(null)

        LaunchedEffect(mode) {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupDialog: opened, mode=$mode, files=${files.size}, managerAvailable=${backupManager != null}"
            }
        }

        StyledDialog(
            state = dialogState,
            title = { StyledText(stringResource(mode.title)) },
            buttons = StyledDialog.Buttons(
                positive = StyledDialog.Button(
                    label = stringResource(Res.string.button_ok),
                    onClick = {
                        if (mode == BackupDialog.Mode.Import && state.value is State.Done) {
                            L.logIf { BackupDefaults.LOG }?.d {
                                "BackupDialog: restore completed, notifying manager that app data was restored"
                            }
                            backupManager!!.onBackupRestored()
                        }
                    },
                    enabled = state.value is State.Done || state.value is State.Error
                )
            ),
            content = StyledDialogDefaults.content {
                StyledColumn(
                    itemSpacing = StyledTheme.spacing.medium
                ) {
                    when (state.value) {
                        State.Done -> {}
                        is State.Error -> {}
                        is State.Running -> StyledLinearProgressIndicator()
                        State.Waiting -> StyledButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                when (mode) {
                                    BackupDialog.Mode.Export -> {
                                        try {
                                            L.logIf { BackupDefaults.LOG }?.d {
                                                "BackupDialog: launching export file picker"
                                            }
                                            createFilePicker.launch(
                                                suggestedName = exportFileName.nameWithoutExtension,
                                                defaultExtension = exportFileName.extension
                                            )
                                        } catch (e: ActivityNotFoundException) {
                                            L.logIf { BackupDefaults.LOG }?.d {
                                                "BackupDialog: export file picker unavailable, error=${e.javaClass.simpleName}"
                                            }
                                            dialogActivityNotFound.show(ActivityNotFoundError.Create)
                                        }
                                    }

                                    BackupDialog.Mode.Import -> {
                                        try {
                                            L.logIf { BackupDefaults.LOG }?.d {
                                                "BackupDialog: launching import file picker"
                                            }
                                            openFilePicker.launch()
                                        } catch (e: ActivityNotFoundException) {
                                            L.logIf { BackupDefaults.LOG }?.d {
                                                "BackupDialog: import file picker unavailable, error=${e.javaClass.simpleName}"
                                            }
                                            dialogActivityNotFound.show(ActivityNotFoundError.Open)
                                        }
                                    }
                                }
                            }) {
                            StyledText(stringResource(Res.string.backup_select_file))
                        }
                    }
                    StyledText(state.value.info())
                }
            }
        )

        if (dialogActivityNotFound.visible) {
            DialogBackupActivityNotFound(dialogActivityNotFound)
        }
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