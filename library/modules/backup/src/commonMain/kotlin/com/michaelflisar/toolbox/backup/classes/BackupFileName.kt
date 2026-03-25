package com.michaelflisar.toolbox.backup.classes

class BackupFileName(
    val nameWithoutExtension: String,
    val extension: String
)  {
    val name: String
        get() = "$nameWithoutExtension.$extension"
}