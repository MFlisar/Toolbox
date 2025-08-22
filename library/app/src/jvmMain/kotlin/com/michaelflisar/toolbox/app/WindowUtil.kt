package com.michaelflisar.toolbox.app

import java.awt.GraphicsEnvironment
import java.awt.Window

object WindowUtil {

    fun isWindowOnScreen(window: Window, partlyOnly: Boolean): Boolean {
        val windowBounds = window.bounds
        for (gd in GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices) {
            val config = gd.defaultConfiguration
            val screenBounds = config.bounds
            if (partlyOnly) {
                if (windowBounds.intersects(screenBounds)) {
                    return true
                }
            } else {
                if (screenBounds.contains(windowBounds)) {
                    return true
                }
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