package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

expect object AdaptiveButtonDefaults {

    val PrimaryButtonShape: Shape
        @Composable
        get

    val PrimaryButtonContentPadding: PaddingValues
        @Composable
        get
}

@Composable
expect fun AdaptivePrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AdaptiveButtonDefaults.PrimaryButtonShape,
    contentPadding: PaddingValues = AdaptiveButtonDefaults.PrimaryButtonContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
)

