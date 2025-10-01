package com.michaelflisar.toolbox

import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object Platform {

    actual val DispatcherIO: CoroutineDispatcher = Dispatchers.Main

    actual val openLanguagePicker: (() -> Unit)? = null
    actual val openMarket: (() -> Unit)? = null

    actual fun Modifier.cursor() : Modifier {
        return this
    }

    actual fun openUrl(url: String) {
        window.open(url, "_blank")
    }
}