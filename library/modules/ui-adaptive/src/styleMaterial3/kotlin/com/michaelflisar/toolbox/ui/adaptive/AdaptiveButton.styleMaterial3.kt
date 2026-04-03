package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

actual object AdaptiveButtonDefaults {

    @Composable
    actual fun ButtonShape(variant: AdaptiveButton.Variant): Shape = when (variant) {
        AdaptiveButton.Variant.Prominent -> ButtonDefaults.shape
        AdaptiveButton.Variant.Default -> ButtonDefaults.filledTonalShape
        AdaptiveButton.Variant.Subtle -> ButtonDefaults.textShape
        AdaptiveButton.Variant.Dangerous -> ButtonDefaults.shape
    }


    @Composable
    actual fun ButtonContentPadding(variant: AdaptiveButton.Variant): PaddingValues =
        when (variant) {
            AdaptiveButton.Variant.Prominent -> ButtonDefaults.ContentPadding
            AdaptiveButton.Variant.Default -> ButtonDefaults.ContentPadding
            AdaptiveButton.Variant.Subtle -> ButtonDefaults.TextButtonContentPadding
            AdaptiveButton.Variant.Dangerous -> ButtonDefaults.ContentPadding
        }
}

@Composable
actual fun AdaptiveButton(
    variant: AdaptiveButton.Variant,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource?,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = when (variant) {
        AdaptiveButton.Variant.Prominent -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )

        AdaptiveButton.Variant.Default -> ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        AdaptiveButton.Variant.Subtle -> ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )

        AdaptiveButton.Variant.Dangerous -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    }

    val interaction = interactionSource ?: MutableInteractionSource()

    when (variant) {
        AdaptiveButton.Variant.Subtle ->
            TextButton(
                onClick = onClick,
                enabled = enabled,
                shape = shape,
                modifier = modifier,
                colors = colors,
                contentPadding = contentPadding,
                interactionSource = interaction,
                content = content
            )

        AdaptiveButton.Variant.Default ->
            FilledTonalButton(
                onClick = onClick,
                enabled = enabled,
                shape = shape,
                modifier = modifier,
                colors = colors,
                contentPadding = contentPadding,
                interactionSource = interaction,
                content = content
            )

        AdaptiveButton.Variant.Prominent,
        AdaptiveButton.Variant.Dangerous,
            ->
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = shape,
                modifier = modifier,
                colors = colors,
                contentPadding = contentPadding,
                interactionSource = interaction,
                content = content
            )
    }
}

