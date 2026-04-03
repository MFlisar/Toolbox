package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object AdaptiveSwitch {
    enum class Variant {
        Default
    }
}

@Composable
expect fun AdaptiveSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    variant: AdaptiveSwitch.Variant = AdaptiveSwitch.Variant.Default,
)