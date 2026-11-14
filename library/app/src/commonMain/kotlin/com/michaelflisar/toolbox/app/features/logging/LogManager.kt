package com.michaelflisar.toolbox.app.features.logging

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.toolbox.app.AppSetup

expect fun <Setup: IFileLoggingSetup> LogManager.createFileLogger(setup: Setup) : ILumberjackLogger

object LogManager {

    fun init() {
        val setup = AppSetup.get()
        if (setup.isDebugBuild) {
            L.enable(minLogLevel = Level.VERBOSE)
        } else {
            L.enable(minLogLevel = Level.DEBUG)
        }
        if (setup.logging.console != null) {
            L.plant(
                implementation = ConsoleLogger(
                    tagTransformer = { tag ->
                        if (setup.logging.console.fixLogTag != null) {
                            setup.logging.console.fixLogTag + (tag?.let { " $it" } ?: "")
                        } else tag
                    }
                )
            )
        }
        setup.logging.file?.let {
            L.plant(createFileLogger(it))
        }
    }
}