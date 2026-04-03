package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer

actual object AdaptiveButtonDefaults {

    @Composable
    actual fun ButtonShape(variant: AdaptiveButton.Variant): Shape =
        when (variant) {
            AdaptiveButton.Variant.Prominent -> ButtonDefaults.shape
            AdaptiveButton.Variant.Default -> ButtonDefaults.textShape
            AdaptiveButton.Variant.Subtle -> ButtonDefaults.textShape
            AdaptiveButton.Variant.Dangerous -> ButtonDefaults.textShape
        }

    @Composable
    actual fun ButtonContentPadding(variant: AdaptiveButton.Variant): PaddingValues =
        when (variant) {
            AdaptiveButton.Variant.Prominent -> ButtonDefaults.ContentPadding
            AdaptiveButton.Variant.Default -> ButtonDefaults.TextButtonContentPadding
            AdaptiveButton.Variant.Subtle -> ButtonDefaults.TextButtonContentPadding
            AdaptiveButton.Variant.Dangerous -> ButtonDefaults.TextButtonContentPadding
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
    val cs = MaterialTheme.colorScheme

    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    // iOS Farben
    val background = when (variant) {
        AdaptiveButton.Variant.Prominent -> cs.primary
        else -> Color.Transparent
    }

    val foreground = when (variant) {
        AdaptiveButton.Variant.Prominent -> cs.onPrimary
        AdaptiveButton.Variant.Dangerous -> cs.error
        else -> cs.primary
    }

    val pressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(pressed) {
        println("Pressed: $pressed")
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "pressScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (pressed) 0.85f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "pressAlpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
            }
            .clip(shape)
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,     // Kein Ripple auf iOS
                enabled = enabled,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides foreground
        ) {
            Row(content = content)
        }
    }
}