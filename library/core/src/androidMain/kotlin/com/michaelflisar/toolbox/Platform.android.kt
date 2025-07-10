package com.michaelflisar.toolbox

import android.os.Build
import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object Platform {

    actual val DispatcherIO: CoroutineDispatcher = Dispatchers.IO

    actual val openLanguagePicker: (() -> Unit)?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            { IntentUtil.openLanguagePicker(AppContext.context()) }
        } else {
            null
        }

    actual val openMarket: (() -> Unit)? = {
        IntentUtil.openMarket(AppContext.context())
    }

    actual val sendFeedback: ((appendLogFiles: Boolean, fileLoggerSetup: IFileLoggingSetup) -> Unit)? = { appendLogFiles, fileLoggerSetup ->
        L.sendFeedback(fileLoggerSetup as FileLoggerSetup, appendLogFile = appendLogFiles)
    }

    actual fun Modifier.cursor() : Modifier {
        return this
    }

    actual fun openUrl(url: String) {
        IntentUtil.openUrl(AppContext.context(), url)
    }
}