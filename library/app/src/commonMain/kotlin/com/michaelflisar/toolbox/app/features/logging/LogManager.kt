package com.michaelflisar.toolbox.app.features.logging

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.toolbox.app.AppSetup

object LogManager {

    fun init() {
        val setup = AppSetup.get()
        if (setup.isDebugBuild) {
            L.enable(minLogLevel = Level.VERBOSE)
        } else {
            L.enable(minLogLevel = Level.DEBUG)
        }
        if (setup.consoleLoggerSetup != null) {
            L.plant(
                implementation = ConsoleLogger(
                    tagTransformer = { tag ->
                        if (setup.consoleLoggerSetup.fixLogTag != null) {
                            setup.consoleLoggerSetup.fixLogTag + (tag?.let { " $it" } ?: "")
                        } else tag
                    }
                )
            )
        }
        setup.fileLogger?.createLogger()?.let { L.plant(it) }
    }
}