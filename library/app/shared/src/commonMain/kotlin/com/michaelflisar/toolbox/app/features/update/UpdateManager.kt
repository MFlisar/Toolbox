package com.michaelflisar.toolbox.app.features.update

import com.michaelflisar.composedialogs.core.DispatcherIO
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UpdateManager(
    private val updates: List<BaseUpdate>,
    private val isFirstRun: (lastVersion: Long) -> Boolean = { lastVersion -> lastVersion <= 0L },
) {

    fun update(scope: CoroutineScope, setup: AppSetup) {
        scope.launch(DispatcherIO) {
            update(setup)
        }
    }

    suspend fun update(setup: AppSetup) {

        var lastVersion = setup.prefs.lastAppVersion.read()
        val currentVersion = setup.appData.versionCode
        L.debug(ToolboxLogging.Tag.UpdateManager) { "lastVersion = $lastVersion => currentVersion = $currentVersion " }
        if (isFirstRun(lastVersion)) {
            L.debug(ToolboxLogging.Tag.UpdateManager) { "UPDATE SKIPPED - FIRST RUN!" }
            lastVersion = currentVersion.toLong()
            setup.prefs.lastAppVersion.update(lastVersion)
        } else if (lastVersion < currentVersion) {
            L.debug(ToolboxLogging.Tag.UpdateManager) { "UPDATES gestartet..." }
            updates
                .sortedBy { it.version }
                .forEach { it.execute(lastVersion) }
            setup.prefs.lastAppVersion.update(currentVersion.toLong())
            L.debug(ToolboxLogging.Tag.UpdateManager) { "UPDATES fertig" }
        } else {
            L.debug(ToolboxLogging.Tag.UpdateManager) { "UPDATE SKIPPED weil lastVersion bereits up-to-date ist!" }
        }

    }
}