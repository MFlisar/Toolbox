package com.michaelflisar.toolbox.windowsapp.ui.dialogs

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.michaelflisar.composecolors.material.MaterialColor
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.ui.MyScrollableColumn
import com.michaelflisar.toolbox.windowsapp.DesktopDialog

object DesktopInfoDialog {

    class Data(
        val dialogTitle: String,
        val title: String,
        val info: String,
        val type: Type = Type.Info
    ) {
        enum class Type(
            val icon: ImageVector
        ) {
            Info(Icons.Default.Info),
            Warning(Icons.Default.Warning),
            Error(Icons.Default.Error),
            Success(Icons.Default.Check)
        }
    }

    class StyleData(
        val colorSuccess: Color,
        val colorWarning: Color,
        val colorError: Color
    )
}

@Composable
fun rememberDesktopInfoStyle(
    colorSuccess: Color = if (isSystemInDarkTheme()) MaterialColor.Green700 else MaterialColor.Green300,
    colorWarning: Color = if (isSystemInDarkTheme()) MaterialColor.Orange700 else MaterialColor.Orange300,
    colorError: Color = MaterialTheme.colorScheme.error
) = DesktopInfoDialog.StyleData(colorSuccess, colorWarning, colorError)

@Composable
fun rememberDesktopInfoDialogData() = remember { mutableStateOf<DesktopInfoDialog.Data?>(null) }

@Composable
fun rememberDesktopInfoDialogSimpleData() = remember { mutableStateOf<String?>(null) }

@Composable
fun DesktopInfoDialog(
    title: String,
    info: MutableState<String?>,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE_SMALL
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
    styleData: DesktopInfoDialog.StyleData = rememberDesktopInfoStyle(),
    showIcon: Boolean = true,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE_SMALL
) {
    val d = data.value
    if (d != null) {
        DesktopDialog(
            title = d.dialogTitle,
            size = size,
            buttons = buttons,
            onDismiss = {
                data.value = null
            }
        ) {
            val typeColor = when (d.type) {
                DesktopInfoDialog.Data.Type.Info -> LocalContentColor.current
                DesktopInfoDialog.Data.Type.Warning -> styleData.colorWarning
                DesktopInfoDialog.Data.Type.Error -> styleData.colorError
                DesktopInfoDialog.Data.Type.Success -> styleData.colorSuccess
            }

            MyScrollableColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
                ) {
                    if (showIcon) {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = d.type.icon,
                            contentDescription = null,
                            tint = typeColor
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
                    ) {
                        Text(d.title, style = MaterialTheme.typography.titleMedium, color = typeColor)
                        Text(d.info, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

    }
}

