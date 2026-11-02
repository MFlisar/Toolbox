package com.michaelflisar.toolbox.ui.adaptive.implementations.fluent2.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michaelflisar.toolbox.ui.adaptive.ProvideCustomStyledContentLocals
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme
import com.michaelflisar.toolbox.ui.adaptive.components.StyledButton
import com.michaelflisar.toolbox.ui.adaptive.darken
import com.michaelflisar.toolbox.ui.adaptive.lighten
import com.michaelflisar.toolbox.ui.adaptive.platform.Platform
import com.michaelflisar.toolbox.ui.adaptive.runtime.InternalComposeStyledApi

class Fluent2Button internal constructor(
    val variantMapping: VariantMapping,
    override val defaults: StyledButton.Defaults,
) : StyledButton.Implementation {

    companion object {

        @Composable
        fun create(
            variantMapping: VariantMapping = DefaultVariantMapping,
            defaults: StyledButton.Defaults = defaults(),
        ) = Fluent2Button(variantMapping, defaults)

        /** Maps emphasis to Primary, the default to Secondary and text to Transparent. */
        val DefaultVariantMapping = VariantMapping(
            emphasized = Variant.Primary,
            standard = Variant.Secondary,
            subtle = Variant.Subtle,
            outlined = Variant.Outline,
            text = Variant.Transparent,
        )

        @Composable
        fun defaults(
            shape: Shape = RoundedCornerShape(4.dp),
        ) = StyledButton.Defaults(shape = shape)
    }

    enum class Variant {
        Primary,
        Secondary,
        Subtle,
        Outline,
        Transparent,
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
        Fluent2Button(
            onClick = onClick,
            variant = variantMapping.get(variant),
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
 * Foundation-based Fluent 2 medium button: 4 dp corners, a 96 x 32 dp minimum
 * footprint, semibold labels and flat, ripple-free interaction states.
 *
 * [StyledTheme.sizes] can increase the minimum height. [customization] overrides
 * the fill/content of filled variants; tint controls text for unfilled variants.
 * Border customization only applies to Secondary and Outline.
 */
@OptIn(InternalComposeStyledApi::class)
@Composable
private fun Fluent2Button(
    onClick: () -> Unit,
    modifier: Modifier,
    variant: Fluent2Button.Variant,
    enabled: Boolean,
    shape: Shape,
    interactionSource: MutableInteractionSource?,
    customization: StyledButton.Customization?,
    content: @Composable RowScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()
    val keyboardFocus = enabled && focused &&
            LocalInputModeManager.current.inputMode == InputMode.Keyboard
    val colors = fluentButtonColors(
        variant = variant,
        enabled = enabled,
        hovered = hovered,
        pressed = pressed,
        customization = customization,
    )
    val background by animateColorAsState(
        colors.background,
        animationSpec = tween(if (enabled) 83 else 0),
        label = "FluentButtonBackground",
    )
    val foreground by animateColorAsState(
        colors.foreground,
        animationSpec = tween(if (enabled) 83 else 0),
        label = "FluentButtonForeground",
    )
    val border by animateColorAsState(
        colors.border,
        animationSpec = tween(if (enabled) 83 else 0),
        label = "FluentButtonBorder",
    )
    val textStyle = StyledTheme.typography.labelMedium.copy(
        color = foreground,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.sp,
    )
    val focusOuter = if (StyledTheme.colors.isDarkTheme) Color.White else Color.Black
    val focusInner = if (StyledTheme.colors.isDarkTheme) Color.Black else Color.White

    // settings
    val minWidth = 96.dp
    val minHeight = maxOf(32.dp, StyledTheme.sizes.minimumInteractiveSize)
    val padding = PaddingValues(
        horizontal = 12.dp,
        vertical = 6.dp
    )
    val spacing = 6.dp

    val useHandIndicator = StyledTheme.platform is Platform.Desktop || StyledTheme.platform == Platform.Web

    ProvideCustomStyledContentLocals(
        contentColor = foreground,
        textStyle = textStyle
    ) {
        Row(
            modifier = modifier
                .defaultMinSize(
                    minWidth = minWidth,
                    minHeight = minHeight,
                )
                .hoverable(interactionSource = source, enabled = enabled)
                .clickable(
                    interactionSource = source,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .clip(shape)
                .background(background, shape)
                .border(1.dp, border, shape)
                .then(
                    if (keyboardFocus) {
                        Modifier.fluentFocusIndicator(shape, focusOuter, focusInner)
                    } else {
                        Modifier
                    }
                )
                .then(if (enabled && useHandIndicator) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
                .padding(padding),
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Immutable
private data class FluentButtonColors(
    val background: Color,
    val foreground: Color,
    val border: Color,
)

@Composable
private fun fluentButtonColors(
    variant: Fluent2Button.Variant,
    enabled: Boolean,
    hovered: Boolean,
    pressed: Boolean,
    customization: StyledButton.Customization?,
): FluentButtonColors {
    val theme = StyledTheme.colors
    val dark = theme.isDarkTheme
    val primary = variant == Fluent2Button.Variant.Primary
    val secondary = variant == Fluent2Button.Variant.Secondary
    val outlined = variant == Fluent2Button.Variant.Outline
    val subtle = variant == Fluent2Button.Variant.Subtle
    val text = variant == Fluent2Button.Variant.Transparent
    val filled = primary || secondary
    val neutralForeground = if (dark) Color.White else Color(0xFF242424)
    val neutralSecondaryForeground = if (dark) Color(0xFFD6D6D6) else Color(0xFF424242)
    val transparent = Color.Transparent
    // Keep overlay RGB constant: interpolating transparent black to opaque gray flashes.
    val overlay = if (dark) Color.White else Color.Black
    val idleBackground = if (!filled) overlay.copy(alpha = 0f) else transparent

    if (!enabled) {
        return FluentButtonColors(
            background = if (filled) {
                if (dark) Color(0xFF141414) else Color(0xFFF0F0F0)
            } else {
                idleBackground
            },
            foreground = if (dark) Color(0xFF5C5C5C) else Color(0xFFBDBDBD),
            border = if (secondary || outlined) {
                if (dark) Color(0xFF424242) else Color(0xFFE0E0E0)
            } else {
                transparent
            },
        )
    }

    val tint = customization?.tint?.resolve()?.takeIf { it != Color.Unspecified }
    val onTint = customization?.onTint?.resolve()?.takeIf { it != Color.Unspecified }
    val customBorder = customization?.border?.resolve()?.takeIf { it != Color.Unspecified }
    val baseBackground = when {
        primary -> tint ?: theme.primary
        secondary -> tint ?: if (dark) Color(0xFF292929) else Color.White
        else -> idleBackground
    }
    val baseForeground = when {
        primary -> onTint ?: theme.onPrimary
        secondary -> onTint ?: neutralForeground
        else -> tint ?: if (outlined) neutralForeground else neutralSecondaryForeground
    }
    val baseBorder = if (secondary || outlined) {
        customBorder ?: if (dark) Color(0xFF666666) else Color(0xFFD1D1D1)
    } else {
        transparent
    }
    val active = pressed || hovered
    val background = when {
        !active -> baseBackground
        primary -> when {
            pressed -> baseBackground.darken(0.24f)
            dark -> baseBackground.lighten(0.10f)
            else -> baseBackground.darken(0.10f)
        }

        secondary -> if (dark) {
            if (pressed) baseBackground.darken(0.25f) else baseBackground.lighten(0.09f)
        } else {
            baseBackground.darken(if (pressed) 0.12f else 0.04f)
        }

        subtle || outlined || text -> overlay.copy(
            alpha = if (pressed) {
                if (dark) 0.10f else 0.12f
            } else {
                if (dark) 0.06f else 0.04f
            },
        )

        else -> transparent
    }
    val foreground = when {
        active && variant == Fluent2Button.Variant.Transparent ->
            tint ?: if (pressed) theme.primary.darken(0.24f) else theme.primary

        active && variant == Fluent2Button.Variant.Subtle && tint == null ->
            neutralForeground

        else -> baseForeground
    }
    val stroke = when {
        active && (secondary || outlined) && customBorder == null ->
            if (dark) baseBorder.lighten(0.10f) else baseBorder.darken(if (pressed) 0.14f else 0.05f)

        else -> baseBorder
    }
    return FluentButtonColors(background, foreground, stroke)
}

private fun Modifier.fluentFocusIndicator(
    shape: Shape,
    outer: Color,
    inner: Color,
) = drawWithCache {
    val inset = 2.dp.toPx()
    val outline = shape.createOutline(size, layoutDirection, this)
    val innerOutline = shape.createOutline(
        Size(
            (size.width - 2 * inset).coerceAtLeast(0f),
            (size.height - 2 * inset).coerceAtLeast(0f)
        ),
        layoutDirection,
        this,
    )
    onDrawWithContent {
        drawContent()
        drawOutline(outline, outer, style = Stroke(4.dp.toPx()))
        translate(inset, inset) {
            drawOutline(innerOutline, inner, style = Stroke(1.dp.toPx()))
        }
    }
}