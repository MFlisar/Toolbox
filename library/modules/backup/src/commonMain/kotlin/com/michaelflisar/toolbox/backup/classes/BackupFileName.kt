package com.michaelflisar.toolbox.backup.classes

import androidx.compose.runtime.Immutable

@Immutable
class BackupFileName(
    val nameWithoutExtension: String,
    val extension: String
)  {
    val name: String
        get() = "$nameWithoutExtension.$extension"
}