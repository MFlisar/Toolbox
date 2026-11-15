package com.michaelflisar.toolbox

import platform.AppKit.NSPasteboard
import platform.AppKit.NSPasteboardTypeString
import platform.AppKit.NSWorkspace
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSURL
import platform.Foundation.currentLocale

actual object Platform {

    actual val openLanguagePicker: (() -> Unit)? = null
    actual val openMarket: (() -> Unit)? = null

    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null) {
            NSWorkspace.sharedWorkspace.openURL(nsUrl)
        }
    }

    actual fun setClipboardText(text: String, label: String) {
        NSPasteboard.generalPasteboard.clearContents()
        NSPasteboard.generalPasteboard.setString(text, forType = NSPasteboardTypeString)
    }
}

actual fun Platform.getDecimalSeparator(): Char {
    val locale = NSLocale.currentLocale
    val formatter = NSNumberFormatter()
    formatter.locale = locale
    val separator = formatter.decimalSeparator
    return separator.firstOrNull() ?: '.'
}