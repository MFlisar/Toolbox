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
fun DesktopInfoDialog(
    data: MutableState<DesktopInfoDialog.Data?>,
    size: DpSize = ToolboxDefaults.DEFAULT_SMALL_DIALOG_SIZE,
) {
    val d = data.value
    if (d != null) {
        DesktopDialog(
            title = d.title,
            size = size,
            onDismiss = {
                data.value = null
            }
        ) {
            Text(d.text)
        }
    }
}

