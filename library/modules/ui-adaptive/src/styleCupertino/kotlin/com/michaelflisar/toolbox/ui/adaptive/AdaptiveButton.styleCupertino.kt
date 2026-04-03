package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

actual object AdaptiveButtonDefaults {

    actual val PrimaryButtonShape: Shape
        @Composable
        get() = ButtonDefaults.textShape

    actual val PrimaryButtonContentPadding: PaddingValues
        @Composable
        get() = ButtonDefaults.TextButtonContentPadding
}

@Composable
actual fun AdaptivePrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource?,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = null,
        border = null,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}