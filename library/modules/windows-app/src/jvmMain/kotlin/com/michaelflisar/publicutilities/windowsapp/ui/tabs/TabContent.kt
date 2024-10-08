package com.michaelflisar.publicutilities.windowsapp.ui.tabs

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
    selectedTab: MutableState<TabItem.Item>,
    page: @Composable (item: TabItem.Item) -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = selectedTab.value,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) { targetState ->
        page(targetState)
    }
}