package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.windows.DesktopDialog
import com.michaelflisar.toolbox.windows.ui.dialogs.DesktopInfoDialog
import com.michaelflisar.toolbox.windows.ui.dialogs.DesktopListDialog
import com.michaelflisar.toolbox.windows.ui.dialogs.rememberDesktopInfoDialogData
import com.michaelflisar.toolbox.windows.ui.dialogs.rememberDesktopInfoDialogSimpleData

@Composable
fun ContentPageDialogs() {
    val showDialog = remember { mutableStateOf(false) }
    val showDialog2 = remember { mutableStateOf(false) }
    val showDialog3 = rememberDesktopInfoDialogData()
    val showDialog4 = rememberDesktopInfoDialogSimpleData()

    MyColumn(
        modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
    ) {
        Text("Content")
        Button(
            onClick = {
                showDialog.value = true
            }
        ) {
            Text("Show Dialog")
        }
        Button(
            onClick = {
                showDialog2.value = true
            }
        ) {
            Text("Show ListDialog")
        }
        Button(
            onClick = {
                showDialog3.value = DesktopInfoDialog.Data(
                    "Title",
                    "Important",
                    "Some info...",
                    type = DesktopInfoDialog.Data.Type.Error
                )
            }
        ) {
            Text("Show InfoDialog")
        }
        Button(
            onClick = {
                showDialog4.value = "Info..."
            }
        ) {
            Text("Show Dialog with Buttons")
        }
    }


    DesktopDialog("Dialog", showDialog) {
        Text("Text")
    }

    DesktopListDialog(
        title = "Liste",
        visible = showDialog2,
        items = List(20) { it.toString() },
        onFilter = { item, filter -> item.contains(filter, true) },
        onItemSelected = {
            // ...
        },
        footer = {
            Text(modifier = Modifier.fillMaxWidth(), text = "FOOTER", textAlign = TextAlign.Center)
        }
    ) {
        Text(it)
    }

    DesktopInfoDialog(
        data = showDialog3
    )

    DesktopInfoDialog(
        title = "Dialog",
        info = showDialog4,
        buttons = DesktopDialog.Buttons.TwoButtons(
            label1 = "Yes",
            label2 = "No",
            on1Clicked = {},
            on2Clicked = {}
        )
    )
}