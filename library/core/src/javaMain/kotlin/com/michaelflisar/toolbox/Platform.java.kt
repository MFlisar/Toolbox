package com.michaelflisar.toolbox

actual fun Platform.getDecimalSeparator(): Char {
    return java.text.DecimalFormatSymbols.getInstance(java.util.Locale.getDefault()).decimalSeparator
}
