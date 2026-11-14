package com.michaelflisar.toolbox.app.features.backhandler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import java.awt.AWTEvent
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

object JvmBackHandlerUtil {

    @Composable
    fun ProvideMouseBackHandler() {
        DisposableEffect(Unit) {
            val listener = registerMouseBackButtonListener()
            onDispose {
                unregisterMouseBackButtonListener(listener)
            }
        }
    }

    private fun registerMouseBackButtonListener(): AWTEventListener {
        val listener = AWTEventListener { event ->
            if (event is MouseEvent && event.button == 4 && event.id == MouseEvent.MOUSE_RELEASED) {
                // BUTTON4 ist meist der "Back"-Button
                SwingUtilities.invokeLater {
                    val robot = Robot()
                    robot.keyPress(KeyEvent.VK_ESCAPE)
                    robot.keyRelease(KeyEvent.VK_ESCAPE)
                }
            }
        }
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK)
        return listener
    }

    private fun unregisterMouseBackButtonListener(listener: AWTEventListener) {
        Toolkit.getDefaultToolkit().removeAWTEventListener(listener)
    }
}