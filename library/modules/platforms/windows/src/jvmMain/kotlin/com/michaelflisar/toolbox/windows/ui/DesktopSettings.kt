package com.michaelflisar.toolbox.windows.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.composables.PreferenceItemSetup
import com.michaelflisar.composepreferences.core.composables.PreferenceItemSetupDefaults
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.core.styles.ModernStyle
import com.michaelflisar.composepreferences.screen.input.PreferenceInputText
import com.michaelflisar.kotpreferences.compose.asMutableState
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting

@Composable
fun DesktopSettings(
    modifier: Modifier = Modifier,
    content: @Composable PreferenceGroupScope.() -> Unit,
) {
    PreferenceScreen(
        modifier = modifier,
        settings = PreferenceSettingsDefaults.settings(
            style = ModernStyle.create(
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                foregroundColor = MaterialTheme.colorScheme.onSurface
            ),
            toggleBooleanOnItemClick = true
        )
    ) {
        content()
    }
}

@Composable
fun defaultContentTrailingSize() = PreferenceItemSetupDefaults.trailingContentSize(
    minWidth = 96.dp,
    maxWidth = 96.dp * 4
)

@Composable
fun PreferenceScope.DesktopPrefText(
    title: String,
    setting: StorageSetting<String>,
    icon: ImageVector? = null,
    subtitle: String? = null,
) {
    val state = setting.asMutableState()
    PreferenceInputText(
        value = state.value ?: "",
        onValueChange = {
            state.value = it
        },
        title = title,
        subtitle = subtitle,
        icon = icon?.let {
            { Icon(it, null) }
        },
        itemSetup = PreferenceItemSetup(
            trailingContentSize = defaultContentTrailingSize()
        )
    )
}