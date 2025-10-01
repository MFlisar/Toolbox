package com.michaelflisar.toolbox

import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import java.awt.Cursor
import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.awt.Desktop
import java.net.URI

actual object Platform {

    actual val DispatcherIO: CoroutineDispatcher = Dispatchers.IO

    actual val openLanguagePicker: (() -> Unit)? = null
    actual val openMarket: (() -> Unit)? = null

    actual fun Modifier.cursor() : Modifier {
        return pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)))
    }

    actual fun openUrl(url: String) {
        Desktop.getDesktop().browse(URI(url))
    }
}