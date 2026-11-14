package com.michaelflisar.toolbox.app.j

import androidx.compose.runtime.Composable

@Composable
internal fun JApp(
    content: @Composable () -> Unit,
) {
    content()
}