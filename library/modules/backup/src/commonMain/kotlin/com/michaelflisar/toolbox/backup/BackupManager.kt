package com.michaelflisar.toolbox.backup

import com.michaelflisar.lumberjack.core.L

object BackupManager {

    private var _manager: BackupManagerImpl? = null
    val manager: BackupManagerImpl?
        get() = _manager

    fun init(manager: BackupManagerImpl) {
        _manager = manager
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManager.init: registered manager, autoBackupEnabled=${manager.autoBackupConfig != null}"
        }
    }
}