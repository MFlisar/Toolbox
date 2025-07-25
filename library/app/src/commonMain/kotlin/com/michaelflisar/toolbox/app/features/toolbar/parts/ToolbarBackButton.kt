package com.michaelflisar.toolbox.app.features.toolbar.parts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun ToolbarBackButton(
    showBackButton: Boolean,
    onClick: () -> Unit,
) {
    if (showBackButton) {
        IconButton(
            onClick = onClick
        ) {
            Icon(Icons.Default.ArrowBack, null)
        }
    }
}