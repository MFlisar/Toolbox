package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AdaptiveBottomBar(
    items: List<AdaptiveBottomBarItem>,
    modifier: Modifier
) {
    NavigationBar(modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = item.icon,
                label = item.label?.let { { Text(it) } },
            )
        }
    }
}