package com.michaelflisar.toolbox.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.dialogs.ErrorDialogProvider
import com.michaelflisar.toolbox.app.features.root.Root

@Composable
fun DesktopContainer(
    menuBar: @Composable () -> Unit = {},
    statusBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    ErrorDialogProvider {
        val appState = LocalAppState.current
        Column {
            menuBar()
            Root(
                appState = appState,
                setRootLocals = false
            ) {
                Column(modifier = Modifier) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        content()
                    }
                    statusBar()
                }
            }
        }
    }
}