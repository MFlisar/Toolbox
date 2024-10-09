package com.michaelflisar.toolbox.windowsapp.ui.tabs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun TabContent(
    modifier: Modifier,
    selectedTab: MutableState<Int>,
    page: @Composable (tab: Int) -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = selectedTab.value,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) { targetState ->
        page(targetState)
    }
}