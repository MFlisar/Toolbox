package com.michaelflisar.toolbox

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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

    actual fun setClipboardText(text: String, label: String) {
        val context = PlatformContextProvider.get()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }
}