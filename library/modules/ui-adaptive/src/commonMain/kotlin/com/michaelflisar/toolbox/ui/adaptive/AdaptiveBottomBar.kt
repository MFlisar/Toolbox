package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class AdaptiveBottomBarItem(
    val icon: @Composable () -> Unit,
    val selected: Boolean,
    val onClick: () -> Unit,
    val label: String? = null,
)

@Composable
expect fun AdaptiveBottomBar(
    items: List<AdaptiveBottomBarItem>,
    modifier: Modifier = Modifier
)