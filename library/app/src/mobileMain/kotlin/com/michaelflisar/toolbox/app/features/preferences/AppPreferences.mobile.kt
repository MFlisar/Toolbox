package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.PreferenceSection
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_consent_settings
import com.michaelflisar.toolbox.core.resources.settings_gdpr_consent_show_dialog
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun PreferenceScope.PreferenceRegionAds() {

    val adsManager = AdsManager.manager
    if (adsManager != null) {
        val consent = adsManager.rememberConsent()
        val privacyOptionsRequired = adsManager.privacyOptionsRequired(consent)
        if (privacyOptionsRequired) {
            PreferenceButton(
                onClick = {
                    adsManager.showPrivacyOptionsForm(consent)
                },
                title = stringResource(Res.string.settings_consent_settings),
                subtitle = stringResource(Res.string.settings_gdpr_consent_show_dialog),
                icon = {
                    Icon(
                        Icons.Outlined.PrivacyTip,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
internal actual fun PreferenceGroupScope.PreferenceRegionAdsDeveloper() {

    val adsManager = AdsManager.manager
    if (adsManager != null) {
        val consent = adsManager.rememberConsent()
        PreferenceSection(
            title = "Ads"
        ) {
            PreferenceButton(
                onClick = {
                    adsManager.resetConsent(consent)
                },
                title = "Consent zurücksetzen",
                icon = {
                    Icon(
                        Icons.Outlined.HideSource,
                        contentDescription = null
                    )
                }
            )
        }
    }
}