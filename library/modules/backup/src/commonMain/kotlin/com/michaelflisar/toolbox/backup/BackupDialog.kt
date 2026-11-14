package com.michaelflisar.toolbox.backup

import androidx.compose.runtime.Composable
import com.michaelflisar.composestyled.ui.state.DataDialogState
import com.michaelflisar.toolbox.backup.classes.BackupFileName
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.backup_export
import com.michaelflisar.toolbox.core.resources.backup_exporting
import com.michaelflisar.toolbox.core.resources.backup_import
import com.michaelflisar.toolbox.core.resources.backup_importing
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import org.jetbrains.compose.resources.StringResource

object BackupDialog {

    enum class Mode(
        val title: StringResource,
        val running: StringResource,
    ) {
        Export(Res.string.backup_export, Res.string.backup_exporting),
        Import(Res.string.backup_import, Res.string.backup_importing),
    }
}

@Composable
expect fun BackupDialog(
    dialogState: DataDialogState<BackupDialog.Mode>,
    exportFileName: BackupFileName,
    files: List<ZipFileContent>,
    backupManager: BackupManagerImpl?,
    fileKitDialogSettings: FileKitDialogSettings,
    extension: String = "zip",
)