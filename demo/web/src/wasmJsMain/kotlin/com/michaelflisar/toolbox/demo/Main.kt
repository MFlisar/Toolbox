package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.michaelflisar.toolbox.components.MyCheckbox

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("Toolbox Wasm Demo", canvasElementId = "ComposeTarget") {
        MaterialTheme {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Hello World")
                val checked = remember { mutableStateOf(false) }
                MyCheckbox(modifier = Modifier.width(200.dp), title = "Checkbox", checked = checked)
            }
        }
    }
}
