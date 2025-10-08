package com.michaelflisar.toolbox.app.features.preferences.groups

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.classes.Dependency
import com.michaelflisar.composepreferences.core.classes.LocalPreferenceSettings
import com.michaelflisar.composepreferences.core.composables.PreferenceItemDefaults
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_title
import com.michaelflisar.toolbox.core.resources.settings_pro_version
import com.michaelflisar.toolbox.core.resources.settings_pro_version_is_free
import com.michaelflisar.toolbox.core.resources.settings_pro_version_is_free_with_click_info2
import com.michaelflisar.toolbox.core.resources.settings_pro_version_is_pro_info
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Crown
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceGroupScope.SettingsProVersionHeader() {

    val proVersionManager = ProVersionManager.setup
    val proState = proVersionManager.proState.collectAsState()

    if (proVersionManager.supported) {

        when (proState.value) {
            ProState.No -> SettingsNotPro(
                proState,
                stringResource(Res.string.settings_pro_version_is_free_with_click_info2)
            )

            ProState.Unknown -> SettingsNotPro(proState, null)
            ProState.Yes -> {
                SettingsPro()
            }
        }
    }
}

@Composable
private fun PreferenceGroupScope.SettingsNotPro(
    proState: State<ProState>,
    info: String?
) {
    val appState = LocalAppState.current
    PreferenceButton(
        onClick = {
            appState.showProVersionDialog.show()
        },
        title = stringResource(if (info == null) Res.string.settings_pro_version else Res.string.settings_pro_version_is_free),
        subtitle = info ?: "",
        enabled = Dependency.State(proState) { it == ProState.No },
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = FontAwesomeIcons.Solid.Crown,
                contentDescription = null
            )
        },
        itemStyle = LocalPreferenceSettings.current.style.defaultItemStyle.copy(
            colors = PreferenceItemDefaults.colors(
                backgroundColor = MaterialTheme.colorScheme.primary,
                foregroundColor = MaterialTheme.colorScheme.onPrimary,
            )
        )
    )
}

@Composable
private fun PreferenceGroupScope.SettingsPro() {
    PreferenceInfo(
        title = stringResource(Res.string.dlg_pro_version_title),
        subtitle = stringResource(Res.string.settings_pro_version_is_pro_info),
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = FontAwesomeIcons.Solid.Crown,
                contentDescription = null
            )
        },
        itemStyle = LocalPreferenceSettings.current.style.defaultItemStyle.copy(
            colors = PreferenceItemDefaults.colors(
                backgroundColor = MaterialTheme.colorScheme.primary,
                foregroundColor = MaterialTheme.colorScheme.onPrimary,
            )
        )
    )
}