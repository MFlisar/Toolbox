package com.michaelflisar.toolbox.features.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.placeholder_ads
import org.jetbrains.compose.resources.StringResource

interface BaseAdManager {

    @Composable
    fun Init()

    @Composable
    fun rememberConsent(): Any

    fun privacyOptionsRequired(consent: Any): Boolean

    fun showPrivacyOptionsForm(
        consent: Any,
        onDismissed: () -> Unit = {},
        onError: (Exception) -> Unit = { _ -> },
    )

    fun resetConsent(consent: Any)

    @Composable
    fun AdsBanner(
        adUnitId: String,
        modifier: Modifier = Modifier,
        visible: Boolean = true,
        placeholderResource: StringResource = Res.string.placeholder_ads,
    )
}