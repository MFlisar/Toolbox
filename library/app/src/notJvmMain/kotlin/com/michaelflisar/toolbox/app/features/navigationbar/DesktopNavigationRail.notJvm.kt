package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.toolbox.app.features.navigation.INavItem

@Composable
actual fun JewelNavigation(
    modifier: Modifier,
    items: List<INavItem>,
    selected: (screen: Screen) -> Boolean,
    expanded: MutableState<Boolean>,
    showExpand: Boolean,
) {
    throw RuntimeException("JewelNavigation is not supported on any other platform than JVM!")
}