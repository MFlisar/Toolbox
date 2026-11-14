package com.michaelflisar.toolbox.app.features.toolbar.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.app.LocalAppTheme

@Composable
fun SharedToolbarContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier.background(color = LocalAppTheme.current.toolbarColor)
    ) {
        content()
    }
}