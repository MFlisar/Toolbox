package com.michaelflisar.toolbox.app.features.update

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.logIf

abstract class BaseUpdate(
    val version: Long,
) {
    constructor(
        versionName: String,
    ) : this(AppSetup.get().changelogSetup!!.versionFormatter.parseVersion(versionName).toLong())

    abstract suspend fun update()

    suspend fun execute(lastVersion: Long) {
        if (lastVersion < version) {
            L.logIf(ToolboxLogging.Tag.AppUpdate)?.d { "Konvertierung $version - START" }
            update()
            L.logIf(ToolboxLogging.Tag.AppUpdate)?.d { "Konvertierung $version - END" }
        }
    }

    suspend fun forceExecute() {
        L.logIf(ToolboxLogging.Tag.AppUpdate)?.d { "FORCED Konvertierung $version - START" }
        update()
        L.logIf(ToolboxLogging.Tag.AppUpdate)?.d { "FORCED Konvertierung $version - END" }
    }
}