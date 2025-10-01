package com.michaelflisar.toolbox.backup.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogStateWithData
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composedialogs.dialogs.info.DialogInfo
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.backup.ActivityNotFoundException
import com.michaelflisar.toolbox.backup.BaseBackupManager
import com.michaelflisar.toolbox.backup.ZipFileContent
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.backup_dialog_create_document_activity_not_found_info
import com.michaelflisar.toolbox.core.resources.backup_dialog_create_document_activity_not_found_title
import com.michaelflisar.toolbox.core.resources.backup_dialog_open_document_activity_not_found_info
import com.michaelflisar.toolbox.core.resources.backup_dialog_open_document_activity_not_found_title
import com.michaelflisar.toolbox.core.resources.backup_done
import com.michaelflisar.toolbox.core.resources.backup_error
import com.michaelflisar.toolbox.core.resources.backup_export
import com.michaelflisar.toolbox.core.resources.backup_exporting
import com.michaelflisar.toolbox.core.resources.backup_import
import com.michaelflisar.toolbox.core.resources.backup_importing
import com.michaelflisar.toolbox.core.resources.backup_select_file
import com.michaelflisar.toolbox.core.resources.backup_waiting_for_file
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

object BackupDialog {

    enum class Mode(
        val title: StringResource,
        val running: StringResource,
    ) {
        Export(Res.string.backup_export, Res.string.backup_exporting),
        Import(Res.string.backup_import, Res.string.backup_importing),
    }

    sealed class State {

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

    enum class ActivityNotFoundError {
        Create, Open
    }

    class FileName(
        val nameWithoutExtension: String,
        val extension: String
    )  {
        val name: String
            get() = "$nameWithoutExtension.$extension"
    }

}

@Composable
fun BackupDialog(
    dialogState: DialogStateWithData<BackupDialog.Mode>,
    exportFileName: BackupDialog.FileName,
    files: List<ZipFileContent>,
    backupManager: BaseBackupManager?,
    fileKitDialogSettings: FileKitDialogSettings,
    extension: String = "zip",
) {
    if (dialogState.visible) {

        val mode = dialogState.requireData()

        val state = remember { mutableStateOf<BackupDialog.State>(BackupDialog.State.Waiting) }
        LaunchedEffect(state.value) {
            dialogState.interactionSource.buttonPositiveEnabled.value =
                state.value is BackupDialog.State.Done || state.value is BackupDialog.State.Error
        }

        val selectedFile = remember { mutableStateOf<PlatformFile?>(null) }
        LaunchedEffect(Unit) {
            snapshotFlow { selectedFile.value }
                .collect { file ->
                    if (file != null) {
                        state.value = BackupDialog.State.Running(mode.running)
                        withContext(Platform.DispatcherIO) {
                            val exception = when (mode) {
                                BackupDialog.Mode.Export -> backupManager!!.backup(files, file)
                                BackupDialog.Mode.Import -> backupManager!!.restore(files, file)
                            }
                            state.value = if (exception == null) {
                                BackupDialog.State.Done
                            } else BackupDialog.State.Error(exception)
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

        val dialogActivityNotFound = rememberDialogState<BackupDialog.ActivityNotFoundError>(null)

        Dialog(
            state = dialogState,
            title = { Text(stringResource(mode.title)) },
            onEvent = {
                if (it.isPositiveButton && mode == BackupDialog.Mode.Import && state.value is BackupDialog.State.Done) {
                    backupManager!!.onBackupRestored()
                }
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state.value) {
                    BackupDialog.State.Done -> {}
                    is BackupDialog.State.Error -> {}
                    is BackupDialog.State.Running -> LinearProgressIndicator()
                    BackupDialog.State.Waiting -> Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            when (mode) {
                                BackupDialog.Mode.Export -> {
                                    try {
                                        createFilePicker.launch(exportFileName.nameWithoutExtension, exportFileName.extension)
                                    } catch (_: ActivityNotFoundException) {
                                        dialogActivityNotFound.show(BackupDialog.ActivityNotFoundError.Create)
                                    }
                                }

                                BackupDialog.Mode.Import -> {
                                    try {
                                        openFilePicker.launch()
                                    } catch (_: ActivityNotFoundException) {
                                        dialogActivityNotFound.show(BackupDialog.ActivityNotFoundError.Open)
                                    }
                                }
                            }
                        }) {
                        Text(stringResource(Res.string.backup_select_file))
                    }
                }
                Text(state.value.info())
            }
        }

        if (dialogActivityNotFound.visible) {
            DialogBackupActivityNotFound(dialogActivityNotFound)
        }
    }
}

@Composable
fun DialogBackupActivityNotFound(
    dialogState: DialogStateWithData<BackupDialog.ActivityNotFoundError>,
) {
    val data = dialogState.requireData()
    val title = when (data) {
        BackupDialog.ActivityNotFoundError.Create -> Res.string.backup_dialog_create_document_activity_not_found_title
        BackupDialog.ActivityNotFoundError.Open -> Res.string.backup_dialog_open_document_activity_not_found_title
    }
    val info = when (data) {
        BackupDialog.ActivityNotFoundError.Create -> Res.string.backup_dialog_create_document_activity_not_found_info
        BackupDialog.ActivityNotFoundError.Open -> Res.string.backup_dialog_open_document_activity_not_found_info
    }
    DialogInfo(
        state = dialogState,
        title = { Text(stringResource(title)) },
        info = stringResource(info)
    )
}