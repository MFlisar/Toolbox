package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import com.michaelflisar.toolbox.app.icons.Toolbar
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_toolbar_style
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceScope.PrefToolbarStyle(showIcon: Boolean) {
    val setting = AppSetup.get().prefs.toolbarStyle
    PreferenceList(
        style = PreferenceList.Style.Spinner,
        value = setting.asMutableStateNotNull(),
        items = ToolbarStyle.entries,
        itemTextProvider = { stringResource(it.label) },
        title = stringResource(Res.string.settings_toolbar_style),
        icon = if (showIcon) {
            {
                Icon(
                    Toolbar,
                    contentDescription = null
                )
            }
        } else null
    )
}