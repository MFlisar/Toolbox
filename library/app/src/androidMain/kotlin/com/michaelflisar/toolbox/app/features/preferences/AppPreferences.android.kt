package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_consent_settings
import com.michaelflisar.toolbox.core.resources.settings_gdpr_consent_show_dialog
import com.michaelflisar.toolbox.requireActivity
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun PreferenceScope.PreferenceRegionAds() {

    val context = LocalContext.current

    val adsManager = AdsManager.manager
    if (adsManager != null) {
        val consent = adsManager.rememberConsent(context.requireActivity())
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