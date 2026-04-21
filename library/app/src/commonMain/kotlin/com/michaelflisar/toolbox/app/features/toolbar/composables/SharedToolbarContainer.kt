package com.michaelflisar.toolbox.app.features.toolbar.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.app.features.toolbar.toolbar

@Composable
fun SharedToolbarContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier.background(color = MaterialTheme.colorScheme.toolbar)
    ) {
        content()
    }
}