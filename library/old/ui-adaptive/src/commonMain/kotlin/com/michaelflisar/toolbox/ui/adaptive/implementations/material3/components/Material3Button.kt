package com.michaelflisar.toolbox.ui.adaptive.implementations.material3.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme
import com.michaelflisar.toolbox.ui.adaptive.components.StyledButton
import com.michaelflisar.toolbox.ui.adaptive.implementations.CopyMaterialThemeLocals
import com.michaelflisar.toolbox.ui.adaptive.implementations.CopyStyledLocals
import com.michaelflisar.toolbox.ui.adaptive.implementations.material3.components.Material3Button.Companion.DefaultVariantMapping
import com.michaelflisar.toolbox.ui.adaptive.runtime.InternalComposeStyledApi

class Material3Button internal constructor(
    val variantMapping: VariantMapping,
    override val defaults: StyledButton.Defaults,
) : StyledButton.Implementation {

    companion object {

        /**
         * Creates a Material3Button implementation for StyledButton with the given variant mapping and defaults.
         * @param variantMapping The mapping of StyledButton variants to MaterialButton variants. Defaults to [DefaultVariantMapping].
         * @param defaults The default values for the StyledButton. Defaults to [defaults()].
         * @return A Material3Button implementation for StyledButton.
         */
        @Composable
        fun create(
            variantMapping: VariantMapping = DefaultVariantMapping,
            defaults: StyledButton.Defaults = defaults(),
        ) = Material3Button(
            variantMapping = variantMapping,
            defaults = defaults
        )

        /**
         * default variant mapping for Material3Button
         *  - emphasized -> secondary
         *  - standard -> primary
         *  - subtle -> filled tonal
         *  - outlined -> outlined
         *  - text -> text
         */
        val DefaultVariantMapping = VariantMapping(
            emphasized = Variant.Secondary,
            standard = Variant.Primary,
            subtle = Variant.FilledTonal,
            outlined = Variant.Outlined,
            text = Variant.Text
        )

        @Composable
        fun defaults(
            shape: Shape = RoundedCornerShape(percent = 50),
        ) = StyledButton.Defaults(
            shape = shape
        )
    }

    /**
     * MaterialButton variants
     */
    enum class Variant {
        Primary,
        Secondary,
        FilledTonal,
        Elevated,
        Outlined,
        Text
    }

    @Stable
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

    @OptIn(InternalComposeStyledApi::class)
    @Composable
    override fun Render(
        variant: StyledButton.Variant,
        onClick: () -> Unit,
        modifier: Modifier,
        enabled: Boolean,
        shape: Shape,
        interactionSource: MutableInteractionSource?,
        customization: StyledButton.Customization?,
        content: @Composable (RowScope.() -> Unit),
    ) {
        val variant = variantMapping.get(variant)

        when (variant) {
            Variant.FilledTonal -> {
                FilledTonalButton(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    interactionSource = interactionSource,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = customization?.tint?.resolve() ?: Color.Unspecified,
                        contentColor = customization?.onTint?.resolve() ?: Color.Unspecified,
                    )
                ) {
                    // copy styles from M3 Button to styled locals
                    CopyMaterialThemeLocals(
                        contentColor = LocalContentColor.current
                    ) {
                        content()
                    }
                }
            }

            Variant.Primary -> {
                Button(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    interactionSource = interactionSource,
                    // default: primary button
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customization?.tint?.resolve()
                            ?: StyledTheme.colors.primary,
                        contentColor = customization?.onTint?.resolve()
                            ?: MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    // copy styles from M3 Button to styled locals
                    CopyMaterialThemeLocals(
                        contentColor = LocalContentColor.current
                    ) {
                        content()
                    }
                }
            }

            Variant.Secondary -> {
                Button(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    interactionSource = interactionSource,
                    // default: primary button
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customization?.tint?.resolve()
                            ?: StyledTheme.colors.secondary,
                        contentColor = customization?.onTint?.resolve()
                            ?: StyledTheme.colors.onSecondary
                    )
                ) {
                    // copy styles from M3 Button to styled locals
                    CopyMaterialThemeLocals(
                        contentColor = LocalContentColor.current
                    ) {
                        content()
                    }
                }
            }

            Variant.Elevated -> {
                ElevatedButton(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    interactionSource = interactionSource,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = customization?.tint?.resolve() ?: Color.Unspecified,
                        contentColor = customization?.onTint?.resolve() ?: Color.Unspecified
                    )
                ) {
                    // copy styles from M3 Button to styled locals
                    CopyMaterialThemeLocals(
                        contentColor = LocalContentColor.current
                    ) {
                        content()
                    }
                }
            }

            Variant.Outlined -> {
                // button does not allow to change border color via parameter
                // so we change the base local color instead
                CopyStyledLocals(
                    outlineVariant = customization?.border?.resolve()
                ) {
                    OutlinedButton(
                        onClick = onClick,
                        modifier = modifier,
                        enabled = enabled,
                        shape = shape,
                        interactionSource = interactionSource,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Unspecified,
                            contentColor = customization?.tint?.resolve()
                                ?: MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled)
                    ) {
                        // copy styles from M3 Button to styled locals
                        CopyMaterialThemeLocals(
                            contentColor = LocalContentColor.current
                        ) {
                            content()
                        }
                    }
                }
            }

            Variant.Text -> {
                TextButton(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    interactionSource = interactionSource,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Unspecified,
                        contentColor = customization?.tint?.resolve()
                            ?: MaterialTheme.colorScheme.primary
                    )
                ) {
                    // copy styles from M3 Button to styled locals
                    CopyMaterialThemeLocals(
                        contentColor = LocalContentColor.current
                    ) {
                        content()
                    }
                }
            }
        }
    }
}