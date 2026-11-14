package com.michaelflisar.toolbox

expect object Platform {

    val openLanguagePicker: (() -> Unit)?
    val openMarket: (() -> Unit)?

    fun openUrl(url: String)

    /**
     * label only works on Android, on other platforms it is ignored
     */
    fun setClipboardText(text: String, label: String = "")
}

expect fun Platform.getDecimalSeparator(): Char