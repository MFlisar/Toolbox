package com.michaelflisar.toolbox.proversion.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.ui.layout.StyledColumn
import com.michaelflisar.composestyled.ui.layout.StyledDialog
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.state.VisibilityDialogState
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.button_ok
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_info_already_bought
import com.michaelflisar.toolbox.core.resources.settings_pro_version_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProVersionAlreadyBoughtDialog(
    dialogState: VisibilityDialogState,
) {
    StyledDialog(
        state = dialogState,
        title = { StyledText(stringResource(Res.string.settings_pro_version_title)) },
        buttons = StyledDialog.Buttons(
            positive = StyledDialog.Button(
                label = stringResource(Res.string.button_ok),
                onClick = dialogState::dismiss
            )
        )
    ) { paddingValues ->
        StyledColumn(
            modifier = Modifier.fillMaxWidth().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProVersionHeroGraphic()
            StyledText(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                text = stringResource(Res.string.dlg_pro_version_info_already_bought),
                color = StyledTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = StyledTheme.typography.bodyMedium
            )
        }
    }
}
