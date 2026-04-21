package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.dialogs.ErrorDialogProvider

@Composable
fun AndroidContainer(
    content: @Composable () -> Unit,
) {
    ErrorDialogProvider {
        content()
    }
}