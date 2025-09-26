package com.michaelflisar.toolbox.features.ads

import androidx.compose.runtime.Composable

interface BaseAdManager {

    @Composable
    fun rememberConsent(activity: Any?) : Any

    fun privacyOptionsRequired(consent: Any) : Boolean

    fun showPrivacyOptionsForm(
        consent: Any,
        onDismissed: () -> Unit = {},
        onError: (Exception) -> Unit = { _ -> }
    )
}