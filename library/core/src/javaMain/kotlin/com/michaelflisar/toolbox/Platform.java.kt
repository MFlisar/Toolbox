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

actual fun Platform.getDecimalSeparator(): Char {
    return java.text.DecimalFormatSymbols.getInstance(java.util.Locale.getDefault()).decimalSeparator
}
