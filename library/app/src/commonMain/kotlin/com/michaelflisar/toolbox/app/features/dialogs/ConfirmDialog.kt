package com.michaelflisar.toolbox.app.features.dialogs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.michaelflisar.composedialogs.core.*
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.cancel
import com.michaelflisar.toolbox.core.resources.yes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDialog(
    state: DialogState,
    title: String,
    onConfirm: suspend () -> Unit = {},
    onCancel: suspend () -> Unit = {},
    confirm: String = stringResource(Res.string.yes),
    cancel: String = stringResource(Res.string.cancel),
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    if (state.visible) {
        Dialog(
            state = state,
            title = { Text(title) },
            buttons = DialogDefaults.buttons(
                positive = DialogButton(confirm),
                negative = DialogButton(cancel)
            ),
            onEvent = {
                when (it) {
                    DialogEvent.Dismissed -> {}
                    is DialogEvent.Button -> {
                        when (it.button) {
                            DialogButtonType.Positive -> {
                                scope.launch { onConfirm() }
                            }

                            DialogButtonType.Negative -> {
                                scope.launch { onCancel() }
                            }
                        }
                    }
                }
            }
        ) {
            content()
        }
    }
}