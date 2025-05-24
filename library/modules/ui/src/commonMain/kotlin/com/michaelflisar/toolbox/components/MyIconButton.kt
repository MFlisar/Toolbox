package com.michaelflisar.toolbox.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector

object MyIconButton {
    sealed class Style {

        @Composable
        internal abstract fun Button(
            onClick: () -> Unit,
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            interactionSource: MutableInteractionSource? = null,
            content: @Composable () -> Unit,
        )

        class Default internal constructor(
            val colors: IconButtonColors,
        ) : Style() {

            @Composable
            override fun Button(
                onClick: () -> Unit,
                modifier: Modifier,
                enabled: Boolean,
                interactionSource: MutableInteractionSource?,
                content: @Composable () -> Unit,
            ) {
                IconButton(
                    onClick,
                    modifier,
                    enabled,
                    colors,
                    interactionSource,
                    content
                )
            }
        }

        class Outlined internal constructor(
            val shape: Shape,
            val colors: IconButtonColors,
            val border: BorderStroke?,
        ) : Style() {
            @Composable
            override fun Button(
                onClick: () -> Unit,
                modifier: Modifier,
                enabled: Boolean,
                interactionSource: MutableInteractionSource?,
                content: @Composable () -> Unit,
            ) {
                OutlinedIconButton(
                    onClick,
                    modifier,
                    enabled,
                    shape,
                    colors,
                    border,
                    interactionSource,
                    content
                )
            }
        }

        class FilledIconButton internal constructor(
            val shape: Shape,
            val colors: IconButtonColors,
        ) : Style() {
            @Composable
            override fun Button(
                onClick: () -> Unit,
                modifier: Modifier,
                enabled: Boolean,
                interactionSource: MutableInteractionSource?,
                content: @Composable () -> Unit,
            ) {
                FilledIconButton(
                    onClick,
                    modifier,
                    enabled,
                    shape,
                    colors,
                    interactionSource,
                    content
                )
            }
        }

        class FilledTonal internal constructor(
            val shape: Shape,
            val colors: IconButtonColors,
        ) : Style() {
            @Composable
            override fun Button(
                onClick: () -> Unit,
                modifier: Modifier,
                enabled: Boolean,
                interactionSource: MutableInteractionSource?,
                content: @Composable () -> Unit,
            ) {
                FilledTonalIconButton(
                    onClick,
                    modifier,
                    enabled,
                    shape,
                    colors,
                    interactionSource,
                    content
                )
            }
        }
    }
}

object MyIconButtonDefaults {

    @Composable
    fun styleDefault(
        colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    ): MyIconButton.Style.Default {
        return MyIconButton.Style.Default(colors)
    }

    @Composable
    fun styleOutlined(
        shape: Shape = IconButtonDefaults.outlinedShape,
        colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
        border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled = true),
    ): MyIconButton.Style.Outlined {
        return MyIconButton.Style.Outlined(shape, colors, border)
    }

    @Composable
    fun styleFilled(
        shape: Shape = IconButtonDefaults.filledShape,
        colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    ): MyIconButton.Style.FilledIconButton {
        return MyIconButton.Style.FilledIconButton(shape, colors)
    }

    @Composable
    fun styleFilledTonal(
        shape: Shape = IconButtonDefaults.filledShape,
        colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    ): MyIconButton.Style.FilledTonal {
        return MyIconButton.Style.FilledTonal(shape, colors)
    }
}


@Composable
fun MyIconButton(
    onClick: () -> Unit,
    style: MyIconButton.Style = MyIconButtonDefaults.styleDefault(),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    style.Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    style: MyIconButton.Style = MyIconButtonDefaults.styleDefault(),
    iconPaddingValues: PaddingValues = PaddingValues(),
    tint: Color? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit,
) {
    style.Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Icon(
            modifier = Modifier.padding(iconPaddingValues),
            imageVector = icon,
            contentDescription = null,
            tint = tint ?: LocalContentColor.current
        )
    }
}