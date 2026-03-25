package com.michaelflisar.toolbox.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import app.lexilabs.basic.ads.AdSize
import app.lexilabs.basic.ads.AdState
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.DependsOnGoogleUserMessagingPlatform
import app.lexilabs.basic.ads.composable.BannerAd
import app.lexilabs.basic.ads.composable.ConsentPopup
import app.lexilabs.basic.ads.composable.rememberBannerAd
import app.lexilabs.basic.ads.composable.rememberConsent
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.placeholder_ads
import com.michaelflisar.toolbox.features.proversion.ProState
import org.jetbrains.compose.resources.stringResource

@OptIn(
    DependsOnGoogleUserMessagingPlatform::class,
    DependsOnGoogleMobileAds::class
)
@Composable
fun FooterAdsBanner(
    proState: State<ProState>,
    adUnitId: String,
    modifier: Modifier = Modifier,
) {
    if (proState.value == ProState.No) {
        val consent by rememberConsent()
        ConsentPopup(consent)
        LaunchedEffect(consent.canRequestAds) {
            L.d { "canRequestAds = ${consent.canRequestAds}" }
        }

        val adSize = AdSize.FULL_BANNER

        Box(
            modifier = modifier
        ) {
            if (consent.canRequestAds) {
                val bannerAd by rememberBannerAd(
                    adUnitId = adUnitId,
                    adSize = adSize
                )
                LaunchedEffect(bannerAd.state) {
                    L.d { "bannerAd.state = ${bannerAd.state}" }
                }
                if (bannerAd.state == AdState.READY) {
                    BannerAd(bannerAd)
                } else {
                    ShowAdPlaceholder(adSize)
                }
            } else {
                ShowAdPlaceholder(adSize)
            }
        }
    }
}

@OptIn(DependsOnGoogleMobileAds::class)
@Composable
private fun ShowAdPlaceholder(adSize: AdSize) {
    val width = with(LocalDensity.current) { adSize.width.toDp() }
    val height = with(LocalDensity.current) { adSize.height.toDp() }
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(height)
        //.background(MaterialTheme.colorScheme.surfaceContainer)
        ,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(width, height),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(Res.string.placeholder_ads))
        }
    }
}