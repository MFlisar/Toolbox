package com.michaelflisar.toolbox.app.features.toast

import android.widget.Toast
import com.michaelflisar.kmp.platformcontext.PlatformApplicationContext

actual val showToast: ((message: String, duration: ToastDuration) -> Unit)?
    get() = { message: String, duration: ToastDuration ->
        val duration = when (duration) {
            ToastDuration.Short -> Toast.LENGTH_SHORT
            ToastDuration.Long -> Toast.LENGTH_LONG
        }
        Toast.makeText(PlatformApplicationContext, message, duration).show()
    }