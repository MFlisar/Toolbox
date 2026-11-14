package com.michaelflisar.toolbox.backup

import androidx.compose.runtime.Composable
import com.michaelflisar.composestyled.ui.state.DataDialogState
import com.michaelflisar.toolbox.backup.classes.BackupFileName
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

@Composable
actual fun BackupDialog(
    dialogState: DataDialogState<BackupDialog.Mode>,
    exportFileName: BackupFileName,
    files: List<ZipFileContent>,
    backupManager: BackupManagerImpl?,
    fileKitDialogSettings: FileKitDialogSettings,
    extension: String,
) {
    // empty no-op
}