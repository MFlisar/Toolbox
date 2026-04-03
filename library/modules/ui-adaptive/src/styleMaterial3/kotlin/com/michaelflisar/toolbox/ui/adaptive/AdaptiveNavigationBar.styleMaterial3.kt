package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AdaptiveNavigationBar(
    title: String,
    modifier: Modifier,
    variant: AdaptiveNavigationBar.Variant,
    onBack: (() -> Unit)?,
    actions: @Composable (() -> Unit)?,
) {
    when (variant) {
        AdaptiveNavigationBar.Variant.Standard ->
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    onBack?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    }
                },
                actions = { actions?.invoke() },
                modifier = modifier
            )

        AdaptiveNavigationBar.Variant.LargeTitle ->
            LargeTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    onBack?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    }
                },
                actions = { actions?.invoke() },
                modifier = modifier
            )
    }
}