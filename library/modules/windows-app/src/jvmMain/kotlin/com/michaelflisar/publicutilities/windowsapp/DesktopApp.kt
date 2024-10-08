package com.michaelflisar.publicutilities.windowsapp

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.publicutilities.windowsapp.settings.UISetting
import java.io.File

object DesktopApp {

    internal object Constants {
        val WINDOW_WIDTH = UISetting.Integer("window_width", "", 1024)
        val WINDOW_HEIGHT = UISetting.Integer("window_height", "", 800)
        val WINDOW_X = UISetting.Integer("window_x", "", 0)
        val WINDOW_Y = UISetting.Integer("window_y", "", 0)
        val WINDOW_PLACEMENT = UISetting.Integer("window_placement", "", WindowPlacement.Floating.ordinal)

        val COLORS = lightColorScheme(
            primary = Color(0xff1976d2), // blue
            primaryContainer = Color(0xffc1dcf7),
            secondary = Color(0xff00c853), // green
            secondaryContainer = Color(0xff91f5ba)
        )
    }

    fun initLoggingConsoleOnly() {
        L.init(LumberjackLogger)
        L.plant(ConsoleLogger())
    }

    fun initLogging() : IFileLoggingSetup {
        L.init(LumberjackLogger)
        L.plant(ConsoleLogger())
        val setup = FileLoggerSetup.FileSize.create(
            folder = File(System.getProperty("user.dir")),
            maxFileSizeInBytes = Int.MAX_VALUE
        )
        L.plant(FileLogger(setup))
        return setup
    }

}