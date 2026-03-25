package com.michaelflisar.toolbox

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object Platform {

    actual val DispatcherIO: CoroutineDispatcher = Dispatchers.IO

    actual val openLanguagePicker: (() -> Unit)?
        @SuppressLint("DiscouragedApi")
        get() = if (IntentUtil.supportsLanguagePicker()) {
            { IntentUtil.openLanguagePicker(PlatformContextProvider.get()) }
        } else {
            null
        }

    actual val openMarket: (() -> Unit)? = {
        IntentUtil.openMarket(PlatformContextProvider.get())
    }

    actual fun Modifier.cursor(): Modifier {
        return this
    }

    actual fun openUrl(url: String) {
        IntentUtil.openUrl(PlatformContextProvider.get(), url)
    }
}