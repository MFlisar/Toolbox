package com.michaelflisar.toolbox.backup

object BackupManager {

    private var _manager: BackupManagerImpl? = null
    val manager: BackupManagerImpl?
        get() = _manager

    fun init(manager: BackupManagerImpl) {
        _manager = manager
    }
}