package com.michaelflisar.toolbox

import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.AppKit.NSWorkspace
import platform.Foundation.NSURL

actual object Platform {

    actual val openLanguagePicker: (() -> Unit)? = null
    actual val openMarket: (() -> Unit)? = null

    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null) {
            NSWorkspace.sharedWorkspace.openURL(nsUrl)
        }
    }
}