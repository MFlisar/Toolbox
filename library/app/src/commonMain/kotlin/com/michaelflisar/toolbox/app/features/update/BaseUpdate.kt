package com.michaelflisar.toolbox.app.features.update

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.logIf

abstract class BaseUpdate(
    val version: Long
) {
    constructor(
        versionName: String
    ) : this(CommonApp.setup.changelogSetup!!.versionFormatter.parseVersion(versionName).toLong())

    abstract suspend fun update(context: PlatformContext)

    suspend fun execute(context: PlatformContext, lastVersion: Long) {
        if (lastVersion < version) {
            L.logIf(ToolboxLogging.Tag.AppUpdate)?.d { "Konvertierung $version - START" }
            update(context)
            L.logIf(ToolboxLogging.Tag.AppUpdate)?.d { "Konvertierung $version - END" }
        }
    }

    suspend fun forceExecute(context: PlatformContext) {
        L.logIf(ToolboxLogging.Tag.AppUpdate)?.d { "FORCED Konvertierung $version - START" }
        update(context)
        L.logIf(ToolboxLogging.Tag.AppUpdate)?.d { "FORCED Konvertierung $version - END" }
    }
}