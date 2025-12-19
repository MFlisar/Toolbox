package com.michaelflisar.toolbox.app.features.update

import com.michaelflisar.composedialogs.core.DispatcherIO
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.logIf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UpdateManager(
    private val updates: List<BaseUpdate>,
    private val isFirstRun: (lastVersion: Long) -> Boolean = { lastVersion -> lastVersion <= 0L },
) {

    fun update(scope: CoroutineScope, context: PlatformContext, setup: AppSetup) {
        scope.launch(DispatcherIO) {
            update(context, setup)
        }
    }

    suspend fun update(context: PlatformContext, setup: AppSetup) {

        var lastVersion = setup.prefs.lastAppVersion.read()
        val currentVersion = setup.versionCode
        L.logIf(ToolboxLogging.Tag.UpdateMananger)
            ?.d { "lastVersion = $lastVersion => currentVersion = $currentVersion " }
        if (isFirstRun(lastVersion)) {
            L.logIf(ToolboxLogging.Tag.UpdateMananger)?.d { "UPDATE SKIPPED - FIRST RUN!" }
            lastVersion = currentVersion.toLong()
            setup.prefs.lastAppVersion.update(lastVersion)
        } else if (lastVersion < currentVersion) {
            L.logIf(ToolboxLogging.Tag.UpdateMananger)?.d { "UPDATES gestartet..." }
            updates
                .sortedBy { it.version }
                .forEach { it.execute(context, lastVersion) }
            setup.prefs.lastAppVersion.update(currentVersion.toLong())
            L.logIf(ToolboxLogging.Tag.UpdateMananger)?.d { "UPDATES fertig" }
        } else {
            L.logIf(ToolboxLogging.Tag.UpdateMananger)
                ?.d { "UPDATE SKIPPED weil lastVersion bereits up-to-date ist!" }
        }

    }
}