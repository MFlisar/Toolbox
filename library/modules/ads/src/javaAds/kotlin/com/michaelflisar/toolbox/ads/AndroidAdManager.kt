package com.michaelflisar.toolbox.ads

import android.Manifest
import android.app.Activity
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import app.lexilabs.basic.ads.AdUnitId
import app.lexilabs.basic.ads.BasicAds
import app.lexilabs.basic.ads.Consent
import app.lexilabs.basic.ads.DependsOnGoogleUserMessagingPlatform
import app.lexilabs.basic.ads.ExperimentalBasicAds
import com.michaelflisar.toolbox.features.ads.BaseAdManager

object AndroidAdManager : BaseAdManager {

    object Ids {
        const val BANNER_DEFAULT = AdUnitId.BANNER_DEFAULT
    }


    @RequiresPermission(Manifest.permission.INTERNET)
    fun init(
        activity: Activity
    ) {
        // initialize ads
        BasicAds.initialize(activity)
    }

    @OptIn(DependsOnGoogleUserMessagingPlatform::class, ExperimentalBasicAds::class)
    @Composable
    override fun rememberConsent(activity: Any?) =
        app.lexilabs.basic.ads.composable.rememberConsent(activity)


    @OptIn(DependsOnGoogleUserMessagingPlatform::class, ExperimentalBasicAds::class)
    override fun privacyOptionsRequired(consent: Any): Boolean {
        return (consent as MutableState<Consent>).value.privacyOptionsRequired
    }

    @OptIn(DependsOnGoogleUserMessagingPlatform::class, ExperimentalBasicAds::class)
    override fun showPrivacyOptionsForm(
        consent: Any,
        onDismissed: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        (consent as MutableState<Consent>).value.showPrivacyOptionsForm(onDismissed, onError)
    }
}