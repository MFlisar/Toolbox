package com.michaelflisar.toolbox.windows.jewel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun JewelNavigationContent(
    items: List<IJewelNavigationItem>,
    selected: MutableState<Int>,
    modifier: Modifier = Modifier
 ) {
    AnimatedContent(
        modifier = modifier,
        targetState = selected,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) { targetState ->
        (items.getOrNull(selected.value) as? JewelNavigationItem)?.content?.invoke()
    }
}