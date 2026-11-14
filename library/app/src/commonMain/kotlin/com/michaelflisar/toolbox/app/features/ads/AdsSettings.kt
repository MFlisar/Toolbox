package com.michaelflisar.toolbox.app.features.ads

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.runtime.Composable
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composepreferences.styled.PreferenceItem
import com.michaelflisar.composepreferences.styled.layout.PreferenceSection
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_consent_settings
import com.michaelflisar.toolbox.core.resources.settings_gdpr_consent_show_dialog
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AdsPrivacySettings() {
    val adsManager = AdsManager.manager ?: return
    val consent = adsManager.rememberConsent()
    if (adsManager.privacyOptionsRequired(consent)) {
        PreferenceItem(
            title = stringResource(Res.string.settings_consent_settings),
            subtitle = stringResource(Res.string.settings_gdpr_consent_show_dialog),
            icon = Icons.Outlined.PrivacyTip.toIconComposable(),
            onClick = { adsManager.showPrivacyOptionsForm(consent) }
        )
    }
}

@Composable
internal fun AdsDeveloperSettings() {
    val adsManager = AdsManager.manager ?: return
    val consent = adsManager.rememberConsent()
    PreferenceSection(
        title = "Ads"
    ) {
        PreferenceItem(
            onClick = { adsManager.resetConsent(consent) },
            title = "Consent zurücksetzen",
            icon = Icons.Outlined.HideSource.toIconComposable()
        )
    }
}
