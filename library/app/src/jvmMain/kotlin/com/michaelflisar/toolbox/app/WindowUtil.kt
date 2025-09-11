package com.michaelflisar.toolbox.app

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.logIf
import java.awt.GraphicsEnvironment
import java.awt.Window

object WindowUtil {

    fun isWindowOnScreen(window: Window, partlyOnly: Boolean): Boolean {
        val windowBounds = window.bounds
        for (gd in GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices) {
            val config = gd.defaultConfiguration
            val screenBounds = config.bounds
            if (partlyOnly) {
                val intersects = windowBounds.intersects(screenBounds)
                L.logIf(ToolboxLogging.Tag.Window)?.i { "intersects = $intersects (screenBounds = $screenBounds | windowBounds = $windowBounds)" }
                return intersects
            } else {
                val contains = screenBounds.contains(windowBounds)
                L.logIf(ToolboxLogging.Tag.Window)?.i { "contains = $contains (screenBounds = $screenBounds | windowBounds = $windowBounds)" }
                return contains
            }
        }
        return false
    }

    fun calcCenteredPosition(window: Window): Pair<Int, Int> {
        val screenBounds = window.graphicsConfiguration.bounds
        val windowWidth = window.width
        val windowHeight = window.height
        val x = screenBounds.x + (screenBounds.width - windowWidth) / 2
        val y = screenBounds.y + (screenBounds.height - windowHeight) / 2
        return Pair(x, y)
    }
}