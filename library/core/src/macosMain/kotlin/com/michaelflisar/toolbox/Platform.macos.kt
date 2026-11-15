package com.michaelflisar.toolbox

import platform.AppKit.NSPasteboard
import platform.AppKit.NSPasteboardTypeString
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

    actual fun setClipboardText(text: String, label: String) {
        NSPasteboard.generalPasteboard.clearContents()
        NSPasteboard.generalPasteboard.setString(text, forType = NSPasteboardTypeString)
    }
}