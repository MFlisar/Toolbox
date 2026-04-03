package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object AdaptiveNavigationBar {
    enum class Variant {
        Standard,
        LargeTitle
    }
}

@Composable
expect fun AdaptiveNavigationBar(
    title: String,
    modifier: Modifier = Modifier,
    variant: AdaptiveNavigationBar.Variant = AdaptiveNavigationBar.Variant.Standard,
    onBack: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
)