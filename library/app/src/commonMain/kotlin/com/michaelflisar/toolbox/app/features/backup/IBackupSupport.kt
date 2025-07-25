package com.michaelflisar.toolbox.app.features.backup

import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.backup.ZipFileContent

interface IBackupSupport {

    val backupContent: List<ZipFileContent>
    val prefBackupPath: StorageSetting<String>

    val autoBackup: Boolean
    val extension: String
    val addToPrefs: Boolean


}