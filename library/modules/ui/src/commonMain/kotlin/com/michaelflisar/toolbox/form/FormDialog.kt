package com.michaelflisar.toolbox.form

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Dp
import com.michaelflisar.composedialogs.core.DialogState
import com.michaelflisar.composedialogs.core.ComposeDialogStyle
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButton
import com.michaelflisar.composedialogs.core.DialogButtonType
import com.michaelflisar.composedialogs.core.DialogContentScrollableColumn
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.Options
import com.michaelflisar.composedialogs.core.defaultDialogStyle
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composedialogs.dialogs.info.DialogInfo

object FormDialog {
    class Text(
        val title: (name: String) -> String = { "Edit $it" },
        val save: String = "Save",
        val delete: String = "Delete",
        val confirmDeleteTitle: (name: String) -> String = { "Delete $it" },
        val confirmDeleteText: (name: String) -> String = { "Do you really want to delete this $it?" },
        val confirmDeleteYes: String = "Yes",
        val confirmDeleteNo: String = "Cancel"
    )
}

@Composable
fun FormDialog(
    state: DialogState,
    name: String,
    fields: FormFields,
    labelWidth: Dp? = null,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    confirmDelete: Boolean = true,
    texts: FormDialog.Text = FormDialog.Text(),
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
        val showConfirmDelete = rememberDialogState()
        Dialog(
            state = state,
            title = { Text(texts.title(name)) },
            icon = icon,
            style = style,
            options = options.copy(dismissOnButtonClick = false),
            buttons = DialogDefaults.buttons(
                positive = DialogButton(texts.save),
                negative = DialogButton(texts.delete)
            ),
            onEvent = {
                when (it) {
                    is DialogEvent.Button -> {
                        if (it.button == DialogButtonType.Positive) {
                            // save
                            onSave()
                        } else if (it.button == DialogButtonType.Negative) {
                            // delete
                            if (confirmDelete) {
                                showConfirmDelete.show()
                            } else {
                                onDelete()
                            }
                        }
                        if (options.dismissOnButtonClick && !showConfirmDelete.visible) {
                            state.dismiss()
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

        if (showConfirmDelete.visible) {
            DialogInfo(
                state = showConfirmDelete,
                title = { Text(texts.confirmDeleteTitle(name)) },
                info = texts.confirmDeleteText(name),
                style = style,
                options = options.copy(dismissOnButtonClick = true),
                buttons = DialogDefaults.buttons(
                    positive = DialogButton(texts.confirmDeleteYes),
                    negative = DialogButton(texts.confirmDeleteNo)
                ),
                onEvent = {
                    if (it.isPositiveButton) {
                        onDelete()
                        if (options.dismissOnButtonClick) {
                            state.dismiss()
                        }
                    }
                }
            )
        }
    }
}