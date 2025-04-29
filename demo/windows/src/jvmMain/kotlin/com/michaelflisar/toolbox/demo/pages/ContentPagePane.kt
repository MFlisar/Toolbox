package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.windows.ui.pane.DesktopPaneContainer
import com.michaelflisar.toolbox.windows.ui.pane.rememberDesktopPane

@Composable
fun ContentPagePane() {
    DesktopPaneContainer(
        modifier = Modifier.fillMaxSize(),
        left = rememberDesktopPane("Left") {
            Text("Left")
        },
        right = rememberDesktopPane("Right") {
            Text("Right")
        },
    ) {
        MyColumn(
            modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
        ) {
            Text("Content")
        }
    }
}