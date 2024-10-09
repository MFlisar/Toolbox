package com.michaelflisar.toolbox.windowsapp.ui.dialogs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.windowsapp.DesktopDialog

object DesktopInfoDialog {
    class Data(
        val title: String,
        val text: String
    )
}

@Composable
fun rememberDesktopInfoDialogData() = remember { mutableStateOf<DesktopInfoDialog.Data?>(null) }

@Composable
fun rememberDesktopInfoDialogSimpleData() = remember { mutableStateOf<String?>(null) }

@Composable
fun DesktopInfoDialog(
    title: String,
    info: MutableState<String?>,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    size: DpSize = if (buttons is DesktopDialog.Buttons.None) ToolboxDefaults.DEFAULT_SMALL_DIALOG_SIZE else ToolboxDefaults.DEFAULT_COMPACT_DIALOG_SIZE
) {
    val d = info.value
    if (d != null) {
        DesktopDialog(
            title = title,
            size = size,
            buttons = buttons,
            onDismiss = {
                info.value = null
            }
        ) {
            Text(d)
        }
    }
}

@Composable
fun DesktopInfoDialog(
    data: MutableState<DesktopInfoDialog.Data?>,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    size: DpSize = if (buttons is DesktopDialog.Buttons.None) ToolboxDefaults.DEFAULT_SMALL_DIALOG_SIZE else ToolboxDefaults.DEFAULT_COMPACT_DIALOG_SIZE
) {
    val d = data.value
    if (d != null) {
        DesktopDialog(
            title = d.title,
            size = size,
            buttons = buttons,
            onDismiss = {
                data.value = null
            }
        ) {
            Text(d.text)
        }
    }
}

