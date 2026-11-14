package com.michaelflisar.toolbox

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.michaelflisar.kmp.platformcontext.PlatformApplicationContext

actual object Platform {

    actual val openLanguagePicker: (() -> Unit)?
        @SuppressLint("DiscouragedApi")
        get() = if (IntentUtil.supportsLanguagePicker()) {
            { IntentUtil.openLanguagePicker(PlatformApplicationContext) }
        } else {
            null
        }

    actual val openMarket: (() -> Unit)? = {
        IntentUtil.openMarket(PlatformApplicationContext)
    }

    actual fun openUrl(url: String) {
        IntentUtil.openUrl(PlatformApplicationContext, url)
    }

    actual fun setClipboardText(text: String, label: String) {
        val context = PlatformApplicationContext
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }
}