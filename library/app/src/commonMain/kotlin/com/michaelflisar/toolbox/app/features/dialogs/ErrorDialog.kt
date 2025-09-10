package com.michaelflisar.toolbox.app.features.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.ComposeDialogStyle
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButtons
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.DialogOptions
import com.michaelflisar.composedialogs.core.DialogStateWithData
import com.michaelflisar.composedialogs.core.defaultDialogStyle
import com.michaelflisar.composedialogs.core.rememberDialogState

val LocalErrorDialogState =
    compositionLocalOf<DialogStateWithData<ErrorDialogState>> { throw RuntimeException("ErrorDialogState not initialised!") }

@Stable
class ErrorDialogState internal constructor(
    val title: String,
    val message: String,
    val exception: Exception?
)

@Composable
fun rememberErrorDialogState(): DialogStateWithData<ErrorDialogState> {
    return rememberDialogState(null)
}

@Composable
fun ErrorDialogProvider(
    state: DialogStateWithData<ErrorDialogState> = rememberErrorDialogState(),
    showDialog: Boolean = true,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalErrorDialogState provides state
    ) {
        content()
        if (showDialog && state.visible) {
            ErrorDialog()
        }
    }
}

@Composable
fun ErrorDialog(
    state: DialogStateWithData<ErrorDialogState> = LocalErrorDialogState.current,
    icon: (@Composable () -> Unit)? = null,//{ Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error) },
    style: ComposeDialogStyle = DialogDefaults.defaultDialogStyle(),
    buttons: DialogButtons = DialogDefaults.buttons(),
    options: DialogOptions = DialogDefaults.options(),
    onEvent: (event: DialogEvent) -> Unit = {}
) {
    val data = state.requireData()
    Dialog(
        state = state,
        title = { Text(data.title, color = MaterialTheme.colorScheme.error) },
        icon = icon,
        style = style,
        buttons = buttons,
        options = options,
        onEvent = onEvent
    ) {
        Column {
            Text(data.message)
            if (data.exception != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Exception", style = MaterialTheme.typography.titleMedium)
                Text(data.exception.message ?: data.exception.toString())
            }
        }
    }
}

fun DialogStateWithData<ErrorDialogState>.show(
    title: String,
    message: String,
    exception: Exception? = null
) {
    show(
        ErrorDialogState(
            title = title,
            message = message,
            exception = exception
        )
    )
}

fun DialogStateWithData<ErrorDialogState>.show(
    exception: Exception
) {
    show(
        ErrorDialogState(
            title = exception::class.simpleName ?: "UNKNOWN EXCEPTION",
            message = exception.message ?: "NO ERROR MESSAGE",
            exception = exception
        )
    )
}