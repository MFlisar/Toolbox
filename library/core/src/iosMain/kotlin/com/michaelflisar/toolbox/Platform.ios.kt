package com.michaelflisar.toolbox

import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSURL
import platform.Foundation.currentLocale
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

actual object Platform {

    actual val DispatcherIO: CoroutineDispatcher = Dispatchers.IO

    actual val openLanguagePicker: (() -> Unit)? = null
    actual val openMarket: (() -> Unit)? = null

    actual fun Modifier.cursor() : Modifier {
        return this
    }

    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null) {
            UIApplication.sharedApplication.openURL(nsUrl)
        }
    }

    actual fun setClipboardText(text: String, label: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}

actual fun Platform.getDecimalSeparator(): Char {
    val locale = NSLocale.currentLocale
    val formatter = NSNumberFormatter()
    formatter.locale = locale
    val separator = formatter.decimalSeparator
    return separator.firstOrNull() ?: '.'
}