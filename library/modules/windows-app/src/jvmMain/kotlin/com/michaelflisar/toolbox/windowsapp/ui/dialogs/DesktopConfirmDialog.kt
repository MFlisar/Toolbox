package com.michaelflisar.toolbox.windowsapp.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.DpSize
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.windowsapp.DesktopDialog

@Composable
fun DesktopConfirmDialog(
    title: String,
    visible: MutableState<Boolean>,
    onConfirm: suspend () -> Unit = {},
    onCancel: suspend () -> Unit = {},
    confirm: String = "Ja",
    cancel: String = "Abbrechen",
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE_SMALL,
    content: @Composable () -> Unit
) {
    if (visible.value) {
        DesktopDialog(
            title = title,
            size = size,
            buttons = DesktopDialog.Buttons.TwoButtons(confirm, cancel, onConfirm, onCancel),
            onDismiss = {
                visible.value = false
            }
        ) {
            content()
        }
    }
}