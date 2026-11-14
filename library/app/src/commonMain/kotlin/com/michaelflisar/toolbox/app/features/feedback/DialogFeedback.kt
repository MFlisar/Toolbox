package com.michaelflisar.toolbox.app.features.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.ui.components.StyledCheckbox
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.layout.StyledDialog
import com.michaelflisar.composestyled.ui.layout.StyledDialogDefaults
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.primitives.StyledVerticalSpacer
import com.michaelflisar.composestyled.ui.state.VisibilityDialogState
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.button_cancel
import com.michaelflisar.toolbox.core.resources.feedback_dialog_email_info
import com.michaelflisar.toolbox.core.resources.feedback_dialog_open_email
import com.michaelflisar.toolbox.core.resources.settings_dialog_attach_log_file_details
import com.michaelflisar.toolbox.core.resources.settings_dialog_attach_log_file_label
import com.michaelflisar.toolbox.core.resources.settings_feedback
import org.jetbrains.compose.resources.stringResource

@Composable
fun DialogFeedback(
    state: VisibilityDialogState,
    modifier: Modifier = Modifier,
    style: StyledDialog.Style = StyledDialogDefaults.style,
    properties: StyledDialog.Properties = StyledDialog.Properties(),
    title: (@Composable () -> Unit)? = {
        StyledText(stringResource(Res.string.settings_feedback))
    },
    icon: (@Composable () -> Unit)? = {
        StyledIcon(icon = Icons.Default.MailOutline.toIconComposable())
    },
) {
    val attachLogFile = remember { mutableStateOf(true) }

    StyledDialog(
        state = state,
        modifier = modifier,
        style = style,
        properties = properties,
        title = StyledDialogDefaults.title(icon, title),
        buttons = StyledDialog.Buttons(
            positive = StyledDialog.Button(
                label = stringResource(Res.string.feedback_dialog_open_email),
                onClick = { handleEvent(attachLogFile.value) },
            ),
            negative = StyledDialog.Button(
                label = stringResource(Res.string.button_cancel),
            )
        ),
        content = StyledDialogDefaults.content {
            StyledText(stringResource(Res.string.feedback_dialog_email_info))
            StyledVerticalSpacer(size = StyledTheme.padding.medium)
            StyledCheckbox(
                modifier = Modifier.fillMaxWidth(),
                checked = attachLogFile,
                label = { StyledText(stringResource(Res.string.settings_dialog_attach_log_file_label)) },
                //clickBehavior = StyledToggleBehavior.FullItem
            )
            AnimatedVisibility(attachLogFile.value) {
                StyledVerticalSpacer(size = StyledTheme.padding.medium)
                StyledText(
                    text = stringResource(Res.string.settings_dialog_attach_log_file_details),
                    style = StyledTheme.typography.bodySmall
                )
            }
        }
    )
}

private fun handleEvent(attachLogFile: Boolean) {
    val setup = AppSetup.get()
    FeedbackManager.sendFeedback(
        appName = setup.appData.name,
        fileLoggingSetup = setup.logging.file,
        attachments = emptyList(),
        appendLogFiles = attachLogFile
    )
}