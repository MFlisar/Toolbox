package com.michaelflisar.toolbox.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import app.lexilabs.basic.ads.BasicAds
import app.lexilabs.basic.ads.Consent
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.DependsOnGoogleUserMessagingPlatform
import com.michaelflisar.toolbox.features.ads.BaseAdManager
import org.jetbrains.compose.resources.StringResource

internal object AdManagerImpl : BaseAdManager {

    @OptIn(DependsOnGoogleMobileAds::class)
    @Composable
    override fun Init() {
        // initialize ads
        BasicAds.Initialize()
    }

    @OptIn(DependsOnGoogleUserMessagingPlatform::class)
    @Composable
    override fun rememberConsent() =
        app.lexilabs.basic.ads.composable.rememberConsent()


    @OptIn(DependsOnGoogleUserMessagingPlatform::class)
    override fun privacyOptionsRequired(consent: Any): Boolean {
        return consent.asConsent().value.privacyOptionsRequired
    }

    @OptIn(DependsOnGoogleUserMessagingPlatform::class)
    override fun showPrivacyOptionsForm(
        consent: Any,
        onDismissed: () -> Unit,
        onError: (Exception) -> Unit,
    ) {
        consent.asConsent().value.showPrivacyOptionsForm(onDismissed, onError)
    }

    @OptIn(DependsOnGoogleUserMessagingPlatform::class)
    override fun resetConsent(consent: Any) {
        consent.asConsent().value.reset()
    }

    @OptIn(DependsOnGoogleUserMessagingPlatform::class)
    @Suppress("UNCHECKED_CAST")
    private fun Any.asConsent() = this as MutableState<Consent>

    @Composable
    override fun AdsBanner(
        adUnitId: String,
        modifier: Modifier,
        visible: Boolean,
        placeholderResource: StringResource,
    ) {
        AdsBannerImpl(
            adUnitId = adUnitId,
            modifier = modifier,
            visible = visible,
            placeholderResource = placeholderResource
        )

    }
}