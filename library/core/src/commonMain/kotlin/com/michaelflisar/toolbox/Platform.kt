package com.michaelflisar.toolbox

import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Duration

expect object Platform {

    val DispatcherIO: CoroutineDispatcher

    val openLanguagePicker: (() -> Unit)?
    val openMarket: (() -> Unit)?

    fun Modifier.cursor() : Modifier

    fun openUrl(url: String)
}

expect fun Platform.getDecimalSeparator(): Char