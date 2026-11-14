package com.michaelflisar.toolbox

import java.awt.Desktop
import java.net.URI

actual object Platform {

    actual val openLanguagePicker: (() -> Unit)? = null
    actual val openMarket: (() -> Unit)? = null

    actual fun openUrl(url: String) {
        Desktop.getDesktop().browse(URI(url))
    }

    actual fun setClipboardText(text: String, label: String) {
        val selection = java.awt.datatransfer.StringSelection(text)
        java.awt.Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }
}