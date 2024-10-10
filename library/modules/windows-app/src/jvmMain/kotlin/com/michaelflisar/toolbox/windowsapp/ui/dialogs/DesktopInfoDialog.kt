package com.michaelflisar.toolbox.windowsapp.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.composables.MyHorizontalSpacer
import com.michaelflisar.toolbox.composables.MyVerticalSpacer
import com.michaelflisar.toolbox.windowsapp.DesktopDialog

object DesktopInfoDialog {
    class Data(
        val title: String,
        val text: String,
        val textStyle: TextStyle? = null,
        val textColor: Color = Color.Unspecified,
        val icon: @Composable (() -> Unit)? = null
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
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE_COMPACT
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
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE_COMPACT
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
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (d.icon != null) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        d.icon.invoke()
                    }
                    MyHorizontalSpacer()
                }
                Text(d.text, color = d.textColor, style = d.textStyle ?: LocalTextStyle.current)
            }
        }

    }
}

