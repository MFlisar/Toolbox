package com.michaelflisar.toolbox.app.features.filekit

import androidx.compose.runtime.compositionLocalOf
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

val LocalFileKitDialogSettingsState =
    compositionLocalOf<FileKitDialogSettings> { FileKitDialogSettings.createDefault() }