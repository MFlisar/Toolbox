package com.michaelflisar.toolbox.app.features.logging

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.toolbox.app.CommonApp

object LogManager {

    fun init() {
        val setup = CommonApp.setup
        L.init(LumberjackLogger)
        if (setup.isDebugBuild) {
            L.enable(minLogLevel = Level.DEBUG)
        }
        L.plant(ConsoleLogger())
        setup.fileLogger?.createLogger()?.let { L.plant(it) }
    }

    // TODO
    val sendRelevantFiles: (() -> Unit)? = null
}