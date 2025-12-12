package com.michaelflisar.toolbox.app.features.dialogs

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.michaelflisar.composecolors.material.MaterialColor
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButtons
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogState
import com.michaelflisar.composedialogs.core.DialogStateWithData
import com.michaelflisar.composedialogs.dialogs.info.DialogInfo
import com.michaelflisar.toolbox.app.platform.isAppInDarkTheme
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.ui.MyScrollableColumn

object InfoDialog {

    class Data(
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
fun rememberInfoDialogStyle(
    colorSuccess: Color = if (isAppInDarkTheme()) MaterialColor.Green700 else MaterialColor.Green300,
    colorWarning: Color = if (isAppInDarkTheme()) MaterialColor.Orange700 else MaterialColor.Orange300,
    colorError: Color = MaterialTheme.colorScheme.error
) = InfoDialog.StyleData(colorSuccess, colorWarning, colorError)

@Composable
fun InfoDialog(
    state: DialogState,
    title: String,
    info: String,
    buttons: DialogButtons = DialogDefaults.buttons(),
) {
    if (state.visible) {
        DialogInfo(state = state, title = { Text(title) }, info = info, buttons = buttons)
    }
}

@Composable
fun InfoDialog(
    state: DialogStateWithData<InfoDialog.Data>,
    styleData: InfoDialog.StyleData = rememberInfoDialogStyle(),
    showIcon: Boolean = true,
    buttons: DialogButtons = DialogDefaults.buttons(),
) {
    if (state.visible) {
        val data = state.requireData()
        Dialog(
            state = state,
            title = { Text(data.title) },
            buttons = buttons
        ) {
            val typeColor = when (data.type) {
                InfoDialog.Data.Type.Info -> LocalContentColor.current
                InfoDialog.Data.Type.Warning -> styleData.colorWarning
                InfoDialog.Data.Type.Error -> styleData.colorError
                InfoDialog.Data.Type.Success -> styleData.colorSuccess
            }

            MyScrollableColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                MyRow {
                    if (showIcon) {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = data.type.icon,
                            contentDescription = null,
                            tint = typeColor
                        )
                    }
                    MyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            data.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = typeColor
                        )
                        Text(data.info, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}