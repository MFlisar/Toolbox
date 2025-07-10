package com.michaelflisar.toolbox

import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Duration

expect object Platform {

    val DispatcherIO: CoroutineDispatcher

    val openLanguagePicker: (() -> Unit)?
    val openMarket: (() -> Unit)?
    val sendFeedback: ((appendLogFiles: Boolean, fileLoggerSetup: IFileLoggingSetup) -> Unit)?

    fun Modifier.cursor() : Modifier

    fun openUrl(url: String)
}