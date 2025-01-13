package com.michaelflisar.toolbox.form

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Dp
import com.michaelflisar.composedialogs.core.BaseDialogState
import com.michaelflisar.composedialogs.core.ComposeDialogStyle
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButton
import com.michaelflisar.composedialogs.core.DialogButtonType
import com.michaelflisar.composedialogs.core.DialogContentScrollableColumn
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.Options
import com.michaelflisar.composedialogs.core.defaultDialogStyle

@Composable
fun FormDialog(
    state: BaseDialogState,
    name: String,
    fields: FormFields,
    labelWidth: Dp? = null,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    // dialog
    icon: (@Composable () -> Unit)? = null,
    style: ComposeDialogStyle = DialogDefaults.defaultDialogStyle(),
    options: Options = Options()
) {
    if (state.visible) {
        LaunchedEffect(fields) {
            snapshotFlow { fields.isValid.value }.collect {
                state.enableButton(DialogButtonType.Positive, it)
            }
        }
        Dialog(
            state = state,
            title = { Text("Edit $name") },
            icon = icon,
            style = style,
            options = options,
            buttons = DialogDefaults.buttons(
                positive = DialogButton("Save"),
                negative = DialogButton("Delete")
            ),
            onEvent = {
                when (it) {
                    is DialogEvent.Button -> {
                        if (it.button == DialogButtonType.Positive) {
                            // save
                            onSave()
                        } else if (it.button == DialogButtonType.Negative) {
                            // delete
                            onDelete()
                        }
                    }

                    DialogEvent.Dismissed -> {}
                }
            }
        ) {
            DialogContentScrollableColumn {
                Form(fields, labelWidth = labelWidth)
            }
        }
    }
}