package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composepreferences.core.classes.Dependency
import com.michaelflisar.composepreferences.core.composables.BasePreference
import com.michaelflisar.composepreferences.core.composables.PreferenceItemSetup
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composethemer.picker.ThemePicker
import com.michaelflisar.composethemer.picker.composables.MultiLevelThemeSelectorCollection
import com.michaelflisar.composethemer.picker.composables.MultiLevelThemeSelectorTheme
import com.michaelflisar.composethemer.picker.composables.MultiLevelThemeSelectorVariant
import com.michaelflisar.composethemer.picker.internal.SingleChoice
import com.michaelflisar.toolbox.app.resources.Res
import com.michaelflisar.toolbox.app.resources.settings_filter_themes_label
import com.michaelflisar.toolbox.app.resources.settings_filter_themes_placeholder
import com.michaelflisar.toolbox.app.resources.settings_theme
import com.michaelflisar.toolbox.app.resources.settings_theme_details
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceScope.PrefTheme(
    pickerState: ThemePicker.State,
    multiLevelState: ThemePicker.MultiLevelState
) {
    BasePreference(
        itemSetup = PreferenceItemSetup(
            contentPlacementBottom = true
        ),
        title = stringResource(Res.string.settings_theme),
        subtitle = stringResource(Res.string.settings_theme_details),
        icon = { Icon(Icons.Outlined.Style, contentDescription = null) },
        enabled = Dependency.State(pickerState.isThemeEnabled) { it },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MultiLevelThemeSelectorCollection(
                state = pickerState,
                style = SingleChoice.Style.Dropdown(
                    setup = SingleChoice.SpinnerSetup.Default(
                        showSpinnerForSingleItem = false
                    )
                ),
                multiState = multiLevelState,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MultiLevelThemeSelectorTheme(
                    state = pickerState,
                    multiState = multiLevelState,
                    style = SingleChoice.Style.Dropdown(
                        setup = SingleChoice.SpinnerSetup.Filterable(
                            filter = { item, filter -> item.name.lowercase().contains(filter) },
                            label = stringResource(Res.string.settings_filter_themes_label),
                            placeholder = stringResource(Res.string.settings_filter_themes_placeholder),
                            minItemsToShowFilter = 10,
                            showSpinnerForSingleItem = false
                        )
                    ),
                    modifier = Modifier.weight(1f)
                )
                MultiLevelThemeSelectorVariant(
                    state = pickerState,
                    style = SingleChoice.Style.Dropdown(
                        setup = SingleChoice.SpinnerSetup.Default(
                            showSpinnerForSingleItem = false
                        )
                    ),
                    multiState = multiLevelState,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}