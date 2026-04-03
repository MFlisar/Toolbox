package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

object AdaptiveButton {

    enum class Variant {
        Prominent,
        Default,
        Subtle,
        Dangerous
    }

}

expect object AdaptiveButtonDefaults {

    @Composable
    fun ButtonShape(variant: AdaptiveButton.Variant): Shape

    @Composable
    fun ButtonContentPadding(variant: AdaptiveButton.Variant): PaddingValues
}

@Composable
expect fun AdaptiveButton(
    variant: AdaptiveButton.Variant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AdaptiveButtonDefaults.ButtonShape(variant),
    contentPadding: PaddingValues = AdaptiveButtonDefaults.ButtonContentPadding(variant),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
)

// --------------------
// Variant overloads
// --------------------

@Composable
fun AdaptiveButtonProminent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AdaptiveButtonDefaults.ButtonShape(AdaptiveButton.Variant.Prominent),
    contentPadding: PaddingValues = AdaptiveButtonDefaults.ButtonContentPadding(AdaptiveButton.Variant.Prominent),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    AdaptiveButton(
        variant = AdaptiveButton.Variant.Prominent,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun AdaptiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AdaptiveButtonDefaults.ButtonShape(AdaptiveButton.Variant.Default),
    contentPadding: PaddingValues = AdaptiveButtonDefaults.ButtonContentPadding(AdaptiveButton.Variant.Default),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    AdaptiveButton(
        variant = AdaptiveButton.Variant.Default,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun AdaptiveButtonSubtle(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AdaptiveButtonDefaults.ButtonShape(AdaptiveButton.Variant.Subtle),
    contentPadding: PaddingValues = AdaptiveButtonDefaults.ButtonContentPadding(AdaptiveButton.Variant.Subtle),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    AdaptiveButton(
        variant = AdaptiveButton.Variant.Subtle,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun AdaptiveButtonDangerous(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AdaptiveButtonDefaults.ButtonShape(AdaptiveButton.Variant.Dangerous),
    contentPadding: PaddingValues = AdaptiveButtonDefaults.ButtonContentPadding(AdaptiveButton.Variant.Dangerous),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    AdaptiveButton(
        variant = AdaptiveButton.Variant.Dangerous,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

