package com.michaelflisar.toolbox.ui.adaptive.implementations.cupertino.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ui.adaptive.ProvideCustomStyledContentLocals
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme
import com.michaelflisar.toolbox.ui.adaptive.components.StyledButton
import com.michaelflisar.toolbox.ui.adaptive.platform.Platform
import com.michaelflisar.toolbox.ui.adaptive.runtime.InternalComposeStyledApi

/**
 * Foundation-based, classic iOS button styles, not native UIKit or Liquid Glass.
 * Uses ripple-free press fading, button semantics and a minimum 44 dp target,
 * subject to the constraints supplied by the caller.
 */
class CupertinoButton internal constructor(
    val variantMapping: VariantMapping,
    override val defaults: StyledButton.Defaults,
) : StyledButton.Implementation {

    companion object {

        @Composable
        fun create(
            variantMapping: VariantMapping = DefaultVariantMapping,
            defaults: StyledButton.Defaults = defaults(),
        ): CupertinoButton = remember(variantMapping, defaults) {
            CupertinoButton(variantMapping, defaults)
        }

        val DefaultVariantMapping = VariantMapping(
            emphasized = Variant.Filled,
            standard = Variant.Tinted,
            subtle = Variant.Gray,
            outlined = Variant.Outlined,
            text = Variant.Plain,
        )

        @Composable
        fun defaults(
            shape: Shape = RoundedCornerShape(8.dp),
        ) = StyledButton.Defaults(shape = shape)
    }

    /** Outlined is an adaptive API extension, not a standard UIKit configuration. */
    enum class Variant {
        Filled,
        Tinted,
        Gray,
        Outlined,
        Plain,
    }

    @Immutable
    data class VariantMapping(
        val emphasized: Variant,
        val standard: Variant,
        val subtle: Variant,
        val outlined: Variant,
        val text: Variant,
    ) {
        fun get(variant: StyledButton.Variant) = when (variant) {
            StyledButton.Variant.Emphasized -> emphasized
            StyledButton.Variant.Standard -> standard
            StyledButton.Variant.Subtle -> subtle
            StyledButton.Variant.Outlined -> outlined
            StyledButton.Variant.Text -> text
        }
    }

    @Composable
    override fun Render(
        variant: StyledButton.Variant,
        onClick: () -> Unit,
        modifier: Modifier,
        enabled: Boolean,
        shape: Shape,
        interactionSource: MutableInteractionSource?,
        customization: StyledButton.Customization?,
        content: @Composable RowScope.() -> Unit,
    ) {
        CupertinoButton(
            variant = variantMapping.get(variant),
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            interactionSource = interactionSource,
            customization = customization,
            content = content,
        )
    }
}

/**
 * Tint sets the filled background, the tinted wash, or the unfilled foreground.
 * OnTint overrides content in Filled and Tinted; border applies to Outlined.
 * Disabled styling deliberately uses neutral colors instead of custom accents.
 */
@OptIn(InternalComposeStyledApi::class)
@Composable
private fun CupertinoButton(
    variant: CupertinoButton.Variant,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    interactionSource: MutableInteractionSource?,
    customization: StyledButton.Customization?,
    content: @Composable RowScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()
    val keyboardFocus = enabled && focused &&
            LocalInputModeManager.current.inputMode == InputMode.Keyboard
    val colors = cupertinoButtonColors(variant, enabled, customization)
    val opacity by animateFloatAsState(
        targetValue = if (enabled && pressed) 0.55f else 1f,
        animationSpec = tween(if (!enabled) 0 else if (pressed) 60 else 180),
        label = "CupertinoButtonOpacity",
    )
    val textStyle = StyledTheme.typography.bodyMedium.copy(
        color = colors.foreground,
        fontWeight = if (variant == CupertinoButton.Variant.Filled) {
            FontWeight.SemiBold
        } else {
            FontWeight.Normal
        },
    )
    val minimumSize = maxOf(44.dp, StyledTheme.sizes.minimumInteractiveSize)
    val useHandPointer = StyledTheme.platform is Platform.Desktop ||
            StyledTheme.platform == Platform.Web

    ProvideCustomStyledContentLocals(
        contentColor = colors.foreground,
        textStyle = textStyle,
    ) {
        Row(
            modifier = modifier
                .sizeIn(minWidth = minimumSize, minHeight = minimumSize)
                .clickable(
                    interactionSource = source,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .then(
                    if (enabled && useHandPointer) {
                        Modifier.pointerHoverIcon(PointerIcon.Hand)
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (keyboardFocus) {
                        Modifier.border(2.dp, StyledTheme.colors.primary, shape)
                    } else {
                        Modifier
                    }
                )
                .graphicsLayer { alpha = opacity }
                .clip(shape)
                .background(colors.background, shape)
                .then(
                    if (variant == CupertinoButton.Variant.Outlined) {
                        Modifier.border(1.dp, colors.border, shape)
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Immutable
private data class CupertinoButtonColors(
    val background: Color,
    val foreground: Color,
    val border: Color,
)

@Composable
private fun cupertinoButtonColors(
    variant: CupertinoButton.Variant,
    enabled: Boolean,
    customization: StyledButton.Customization?,
): CupertinoButtonColors {
    val theme = StyledTheme.colors
    val filled = variant == CupertinoButton.Variant.Filled
    val tinted = variant == CupertinoButton.Variant.Tinted
    val gray = variant == CupertinoButton.Variant.Gray
    val hasBackground = filled || tinted || gray
    if (!enabled) {
        val foreground = theme.onSurface.copy(alpha = theme.onSurface.alpha * 0.30f)
        return CupertinoButtonColors(
            background = if (hasBackground) {
                theme.onSurface.copy(alpha = theme.onSurface.alpha * 0.08f)
            } else {
                Color.Transparent
            },
            foreground = foreground,
            border = foreground,
        )
    }

    val tint = customization?.tint?.resolve()?.takeIf { it != Color.Unspecified }
        ?: theme.primary
    val onTint = customization?.onTint?.resolve()?.takeIf { it != Color.Unspecified }
    return CupertinoButtonColors(
        background = when {
            filled -> tint
            tinted -> tint.copy(alpha = tint.alpha * if (theme.isDarkTheme) 0.24f else 0.12f)
            gray -> theme.onSurface.copy(alpha = theme.onSurface.alpha * 0.08f)
            else -> Color.Transparent
        },
        foreground = when {
            filled -> onTint ?: theme.onPrimary
            tinted -> onTint ?: tint
            else -> tint
        },
        border = customization?.border?.resolve()?.takeIf { it != Color.Unspecified } ?: tint,
    )
}
