package com.michaelflisar.toolbox.backup

object BackupManager {

    private var _manager: BaseBackupManager? = null
    val manager: BaseBackupManager?
        get() = _manager

    fun init(manager: BaseBackupManager) {
        _manager = manager
    }
}