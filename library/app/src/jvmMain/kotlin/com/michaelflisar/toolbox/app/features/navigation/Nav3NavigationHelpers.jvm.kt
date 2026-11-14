package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import java.awt.AWTEvent
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

@Composable
internal actual fun PlatformNavigationBackHandler(
    navigationState: String,
    replaceNavigationState: Boolean,
    onMouseForward: () -> Unit,
    onBrowserHistoryStateChanged: (String?) -> Unit,
) {
    val currentOnForward = rememberUpdatedState(onMouseForward)

    DisposableEffect(Unit) {
        val listener = AWTEventListener { event ->
            if (event is MouseEvent && event.id == MouseEvent.MOUSE_RELEASED) {
                when (event.button) {
                    4 -> {
                        event.consume()
                        SwingUtilities.invokeLater {
                            val robot = Robot()
                            robot.keyPress(KeyEvent.VK_ESCAPE)
                            robot.keyRelease(KeyEvent.VK_ESCAPE)
                        }
                    }

                    5 -> {
                        event.consume()
                        SwingUtilities.invokeLater {
                            currentOnForward.value()
                        }
                    }
                }
            }
        }
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK)

        onDispose {
            Toolkit.getDefaultToolkit().removeAWTEventListener(listener)
        }
    }
}

internal actual fun PlatformNavigateBack(
    canGoBack: Boolean,
    hasBrowserHistory: Boolean,
    onBackStack: () -> Unit,
) {
    if (canGoBack) onBackStack()
}

internal actual fun PlatformNavigateHistory(
    delta: Int,
    hasNavigationHistory: Boolean,
    onHistoryNavigation: () -> Unit,
) {
    onHistoryNavigation()
}
