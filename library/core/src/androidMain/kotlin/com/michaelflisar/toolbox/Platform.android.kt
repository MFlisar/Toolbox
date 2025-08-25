package com.michaelflisar.toolbox

import android.annotation.SuppressLint
import android.app.LocaleManager
import android.os.Build
import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object Platform {

    actual val DispatcherIO: CoroutineDispatcher = Dispatchers.IO

    actual val openLanguagePicker: (() -> Unit)?
        @SuppressLint("DiscouragedApi")
        get() = if (IntentUtil.supportsLanguagePicker()) {
            { IntentUtil.openLanguagePicker(AppContext.context()) }
        } else {
            null
        }

    actual val openMarket: (() -> Unit)? = {
        IntentUtil.openMarket(AppContext.context())
    }

    actual val sendFeedback: ((appendLogFiles: Boolean, fileLoggerSetup: IFileLoggingSetup) -> Unit)? = { appendLogFiles, fileLoggerSetup ->
        L.sendFeedback(fileLoggerSetup, appendLogFile = appendLogFiles)
    }

    actual fun Modifier.cursor() : Modifier {
        return this
    }

    actual fun openUrl(url: String) {
        IntentUtil.openUrl(AppContext.context(), url)
    }
}