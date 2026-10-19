package com.michaelflisar.toolbox.app.features.toolbar.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.theme.icon

@Composable
fun ToolbarBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    MyIconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        @Suppress("DEPRECATION")
        Icon(
            modifier = Modifier.size(MaterialTheme.icon.size),
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null
        )
    }
}