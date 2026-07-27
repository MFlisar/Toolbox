package com.michaelflisar.toolbox.app.features.backhandler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

object WasmBackHandlerUtil {

    @Composable
    fun ProvideMouseBackHandler() {
        DisposableEffect(Unit) {
            val listener = registerMouseBackButtonListener()
            onDispose {
                unregisterMouseBackButtonListener(listener)
            }
        }
    }

    private fun registerMouseBackButtonListener(): (Event) -> Unit {
        val listener: (Event) -> Unit = { event ->
            val mouseEvent = event as? MouseEvent
            // 4 = "Back"-Button
            if (mouseEvent?.button?.toInt() == 4) {
                window.history.back()
            }
        }
        window.addEventListener("mousedown", listener)
        return listener
    }

    private fun unregisterMouseBackButtonListener(listener: (Event) -> Unit) {
        window.removeEventListener("mousedown", listener)
    }
}