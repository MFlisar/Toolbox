package com.michaelflisar.toolbox.windowsapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.michaelflisar.toolbox.ToolboxDefaults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DesktopDialog(
    title: String,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE,
    padding: PaddingValues = PaddingValues(ToolboxDefaults.CONTENT_PADDING),
    onDismiss: () -> Unit = {},
    dismissable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    DesktopDialog(title, true, onDismiss, size, padding, dismissable, content)
}

@Composable
fun DesktopDialog(
    title: String,
    visible: MutableState<Boolean>,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE,
    padding: PaddingValues = PaddingValues(16.dp),
    dismissable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    DesktopDialog(
        title,
        visible.value,
        { visible.value = false },
        size,
        padding,
        dismissable,
        content
    )
}

@Composable
fun DesktopDialog(
    title: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE,
    padding: PaddingValues = PaddingValues(16.dp),
    dismissable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        DialogWindow(
            visible = visible,
            title = title,
            state = rememberDialogState(
                width = size.width,
                height = size.height
            ),
            onCloseRequest = {
                if (dismissable) {
                    onDismiss()
                }
            }
        ) {
            //Surface(
            //    modifier = modifier,
            //    shape = MaterialTheme.shapes.medium,
            //    color = MaterialTheme.colorScheme.surface,
            //    contentColor = MaterialTheme.colorScheme.onSurface
            //) {
            Column(
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f, false)
                ) {
                    content()
                }
                //Buttons(scope, positive, negative, enablePositive, onPositive, onDismiss)
            }
            //}
        }
        /*
    val auroraApplicationScope = LocalAuroraApplicationScope.current
    val parentAuroraWindowState = LocalAuroraWindowState.current
    if (visible.value) {
        LaunchedEffect(Unit) {
            parentAuroraWindowState.value = false
        }
        val windowState = androidx.compose.ui.window.rememberWindowState()
        val enabled = remember { mutableStateOf(true) }
        auroraApplicationScope.AuroraWindow(
            skin = businessBlackSteelSkin(),
            windowTitlePaneConfiguration = windowTitlePaneConfiguration,
            state = windowState,
            onCloseRequest = {
                visible.value = false
                parentAuroraWindowState.value = true
            },
            title = title,
            icon = icon,
            resizable = resizable,
            alwaysOnTop = alwaysOnTop,
            menuCommands = menuCommands?.invoke(),
            visible = visible.value,
            iconFilterStrategy = iconFilterStrategy,
            enabled = enabled.value,
            focusable = focusable,
            onPreviewKeyEvent = onPreviewKeyEvent,
            onKeyEvent = onKeyEvent
        ) {
            CompositionLocalProvider(
                LocalAuroraWindowState provides enabled
            ) {
                content()
            }
        }
    }*/
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Buttons(
    scope: CoroutineScope,
    positive: String,
    negative: String?,
    enablePositive: Boolean,
    onPositive: suspend () -> Unit,
    onDismiss: suspend () -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        negative?.let {
            TextButton(
                onClick = { scope.launch { onDismiss() } }
            ) {
                Text(negative)
            }
        }
        TextButton(
            enabled = enablePositive,
            onClick = {
                scope.launch {
                    onPositive()
                    onDismiss()
                }
            }
        ) {
            Text(positive)
        }
    }
}