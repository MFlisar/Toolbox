package com.michaelflisar.toolbox.app.features.update

import com.michaelflisar.composedialogs.core.DispatcherIO
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.classes.PlatformContext
import kotlinx.coroutines.CoroutineScope
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
        L.tag("UPDATE").d { "lastVersion = $lastVersion => currentVersion = $currentVersion " }
        if (isFirstRun(lastVersion)) {
            L.tag("UPDATE").d { "UPDATE SKIPPED - FIRST RUN!" }
            lastVersion = currentVersion.toLong()
            setup.prefs.lastAppVersion.update(lastVersion)
        } else if (lastVersion < currentVersion) {
            L.tag("UPDATE").d { "UPDATES gestartet..." }
            updates
                .sortedBy { it.version }
                .forEach { it.execute(context, lastVersion) }
            setup.prefs.lastAppVersion.update(currentVersion.toLong())
            L.tag("UPDATE").d { "UPDATES fertig" }
        } else {
            L.tag("UPDATE").d { "UPDATE SKIPPED weil lastVersion bereits up-to-date ist!" }
        }

    }
}