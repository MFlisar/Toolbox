package com.michaelflisar.toolbox.ui.adaptive.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.michaelflisar.toolbox.ui.adaptive.classes.ColorToken
import com.michaelflisar.toolbox.ui.adaptive.classes.asColorToken
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledRegistry
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledKey

object StyledButton {

    val Key = StyledKey<Implementation>("StyledButton")

    // -------------
    // defaults
    // -------------

    @Stable
    data class Defaults(
        val shape: Shape,
    )

    // -------------
    // variants
    // -------------

    enum class Variant {
        Emphasized,
        Standard,
        Subtle,
        Outlined,
        Text
    }

    // -------------
    // customization
    // -------------

    fun customize(
        tint: Color? = null,
        onTint: Color? = null,
        border: Color? = null,
    ) = customize(tint?.asColorToken(), onTint?.asColorToken(), border?.asColorToken())

    fun customize(
        tint: ColorToken? = null,
        onTint: ColorToken? = null,
        border: ColorToken? = null,
    ) = Customization(tint, onTint, border)

    @Immutable
    class Customization internal constructor(
        val tint: ColorToken?,
        val onTint: ColorToken?,
        val border: ColorToken?,
    )

    // -------------
    // implementations
    // -------------

    interface Implementation {

        val defaults: Defaults

        @Composable
        fun Render(
            variant: Variant,
            onClick: () -> Unit,
            modifier: Modifier = Modifier,
            enabled: Boolean,
            shape: Shape,
            interactionSource: MutableInteractionSource?,
            customization: Customization?,
            content: @Composable RowScope.() -> Unit,
        )
    }
}

// -------------------------
// public API
// -------------------------

@Composable
fun StyledButton(
    variant: StyledButton.Variant = StyledButton.Variant.Standard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // customisation
    shape: Shape = LocalStyledRegistry.current[StyledButton.Key].defaults.shape,
    customization: StyledButton.Customization? = null,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    LocalStyledRegistry.current[StyledButton.Key].Render(
        variant = variant,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        customization = customization,
        content = content
    )
}

// -------------------------
// public API - convenient overloads for all non standard variants
// (standard works out of the box because of the default value)
// -------------------------

@Composable
fun StyledEmphasizedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // customisation
    shape: Shape = LocalStyledRegistry.current[StyledButton.Key].defaults.shape,
    customization: StyledButton.Customization? = null,
    // content
    content: @Composable RowScope.() -> Unit,
) = StyledButton(
    variant = StyledButton.Variant.Emphasized,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    interactionSource = interactionSource,
    customization = customization,
    content = content
)

@Composable
fun StyledSubtleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // customisation
    shape: Shape = LocalStyledRegistry.current[StyledButton.Key].defaults.shape,
    customization: StyledButton.Customization? = null,
    // content
    content: @Composable RowScope.() -> Unit,
) = StyledButton(
    variant = StyledButton.Variant.Subtle,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    interactionSource = interactionSource,
    customization = customization,
    content = content
)

@Composable
fun StyledOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // customisation
    shape: Shape = LocalStyledRegistry.current[StyledButton.Key].defaults.shape,
    customization: StyledButton.Customization? = null,
    // content
    content: @Composable RowScope.() -> Unit,
) = StyledButton(
    variant = StyledButton.Variant.Outlined,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    interactionSource = interactionSource,
    customization = customization,
    content = content
)

@Composable
fun StyledTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // customisation
    shape: Shape = LocalStyledRegistry.current[StyledButton.Key].defaults.shape,
    customization: StyledButton.Customization? = null,
    // content
    content: @Composable RowScope.() -> Unit,
) = StyledButton(
    variant = StyledButton.Variant.Text,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    interactionSource = interactionSource,
    customization = customization,
    content = content
)