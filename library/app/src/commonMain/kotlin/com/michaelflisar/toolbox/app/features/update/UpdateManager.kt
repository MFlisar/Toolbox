package com.michaelflisar.toolbox.app.features.update

import com.michaelflisar.kmp.platformcontext.PlatformIO
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.versions.Version
import com.michaelflisar.toolbox.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateManager(
    private val versions: List<Version>,
    private val isFirstRun: (lastVersion: Long) -> Boolean = { lastVersion -> lastVersion <= 0L },
) {
    companion object {
        fun create(
            versions: List<Version>,
            isFirstRun: (lastVersion: Long) -> Boolean = { lastVersion -> lastVersion <= 0L },
        ) : UpdateManager {
            return UpdateManager(versions, isFirstRun)
        }
    }

    fun update(scope: CoroutineScope, setup: AppSetup) {
        scope.launch(Dispatchers.PlatformIO) {
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
            versions
                .asSequence()
                .filter { it.versionCode.toLong() > lastVersion && it.versionCode <= currentVersion }
                .sortedBy { it.versionCode }
                .forEach { executeUpdate(it) }
            setup.prefs.lastAppVersion.update(currentVersion.toLong())
            L.debug(ToolboxLogging.Tag.UpdateManager) { "UPDATES fertig" }
        } else {
            L.debug(ToolboxLogging.Tag.UpdateManager) { "UPDATE SKIPPED weil lastVersion bereits up-to-date ist!" }
        }

    }

    fun forceUpdate(scope: CoroutineScope, versions: List<Version>) {
        scope.launch(Dispatchers.PlatformIO) {
            forceUpdate(versions)
        }
    }

    suspend fun forceUpdate(versions: List<Version>) {
        L.debug(ToolboxLogging.Tag.UpdateManager) { "FORCED UPDATES gestartet..." }
        versions
            .sortedBy { it.versionCode }
            .forEach { executeUpdate(it, forced = true) }
        L.debug(ToolboxLogging.Tag.UpdateManager) { "FORCED UPDATES fertig" }
    }

    private suspend fun executeUpdate(version: Version, forced: Boolean = false) {
        val update = version.update ?: return
        val action = if (forced) "FORCED Konvertierung" else "Konvertierung"
        L.debug(ToolboxLogging.Tag.AppUpdate) { "$action ${version.versionCode} - START" }
        update()
        L.debug(ToolboxLogging.Tag.AppUpdate) { "$action ${version.versionCode} - END" }
    }
}