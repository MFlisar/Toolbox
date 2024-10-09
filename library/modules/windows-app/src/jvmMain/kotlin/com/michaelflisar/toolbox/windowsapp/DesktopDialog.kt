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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.michaelflisar.toolbox.ToolboxDefaults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object DesktopDialog {

    sealed class Buttons {

        data object None : Buttons()

        internal class Show(
            val buttons: List<Button>
        ) : Buttons()

        companion object {

            fun OneButton(
                label: String,
                isEnabled: Boolean = true,
                dismissOnClick: Boolean = true,
                onClicked: suspend () -> Unit,
            ): Buttons = Show(
                listOf(Button(label, isEnabled, onClicked, dismissOnClick))
            )

            fun TwoButtons(
                label1: String,
                label2: String,
                on1Clicked: suspend () -> Unit,
                on2Clicked: suspend () -> Unit,
                is1Enabled: Boolean = true,
                is2Enabled: Boolean = true,
                dismissOn1Click: Boolean = true,
                dismissOn2Click: Boolean = true
            ): Buttons = Show(
                listOf(
                    Button(label1, is1Enabled, on1Clicked, dismissOn1Click),
                    Button(label2, is2Enabled, on2Clicked, dismissOn2Click),
                )
            )
        }
    }

    class Button(
        val label: String,
        val enabled: Boolean,
        val onClick: suspend () -> Unit,
        val dismissOnClick: Boolean
    )
}

@Composable
fun DesktopDialog(
    title: String,
    visible: MutableState<Boolean>,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE,
    padding: PaddingValues = PaddingValues(16.dp),
    dismissable: Boolean = true,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    content: @Composable ColumnScope.() -> Unit
) {
    DesktopDialog(
        title,
        visible.value,
        { visible.value = false },
        size,
        padding,
        dismissable,
        buttons,
        content
    )
}

@Composable
fun DesktopDialog(
    title: String,
    visible: Boolean = true,
    onDismiss: () -> Unit = {},
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE,
    padding: PaddingValues = PaddingValues(ToolboxDefaults.CONTENT_PADDING),

    dismissable: Boolean = true,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        val scope = rememberCoroutineScope()
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
                    modifier = Modifier.weight(1f, buttons !is DesktopDialog.Buttons.None)
                ) {
                    content()
                }
                when (buttons) {
                    DesktopDialog.Buttons.None -> {
                        //
                    }

                    is DesktopDialog.Buttons.Show -> {
                        Buttons(scope, buttons, onDismiss)
                    }
                }

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
    buttons: DesktopDialog.Buttons.Show,
    onDismiss: () -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        buttons.buttons.forEach {
            TextButton(
                enabled = it.enabled,
                onClick = {
                    scope.launch {
                        it.onClick()
                        if (it.dismissOnClick) {
                            onDismiss()
                        }
                    }
                }
            ) {
                Text(it.label)
            }
        }
    }
}