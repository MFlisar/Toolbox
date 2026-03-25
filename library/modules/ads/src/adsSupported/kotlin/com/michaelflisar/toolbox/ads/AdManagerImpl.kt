package com.michaelflisar.toolbox.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import app.lexilabs.basic.ads.AdUnitId
import app.lexilabs.basic.ads.BasicAds
import app.lexilabs.basic.ads.Consent
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.DependsOnGoogleUserMessagingPlatform
import com.michaelflisar.toolbox.features.ads.BaseAdManager

object AdManagerImpl : BaseAdManager {

    object Ids {
        val BANNER_DEFAULT = AdUnitId.BANNER_DEFAULT
    }


    @OptIn(DependsOnGoogleMobileAds::class)
    @Composable
    fun init() {
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
}