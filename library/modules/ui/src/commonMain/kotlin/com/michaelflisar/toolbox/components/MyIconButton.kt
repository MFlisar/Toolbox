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

private object MyIconButton {

    sealed class Style {

        @Composable
        abstract fun Button(
            onClick: () -> Unit,
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            interactionSource: MutableInteractionSource? = null,
            content: @Composable () -> Unit,
        )

        class Default constructor(
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
                IconButton(
                    onClick,
                    modifier,
                    enabled,
                    colors,
                    interactionSource,
                    shape,
                    content
                )
            }
        }

        class Outlined constructor(
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

        class FilledIconButton constructor(
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

        class FilledTonal constructor(
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

private object MyIconButtonDefaults {

    @Composable
    fun styleDefault(
        shape: Shape = IconButtonDefaults.standardShape,
        colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    ): MyIconButton.Style.Default {
        return MyIconButton.Style.Default(shape, colors)
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

// -------------------------
// public API
// -------------------------

@Composable
fun MyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    // content
    content: @Composable () -> Unit
) {
    MyIconButton(
        onClick = onClick,
        modifier = modifier,
        style = MyIconButtonDefaults.styleDefault(colors = colors),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyOutlinedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = IconButtonDefaults.outlinedShape,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled = true),
    // content
    content: @Composable () -> Unit
) {
    MyIconButton(
        onClick = onClick,
        modifier = modifier,
        style = MyIconButtonDefaults.styleOutlined(
            shape = shape,
            colors = colors,
            border = border
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    // content
    content: @Composable () -> Unit
) {
    MyIconButton(
        onClick = onClick,
        modifier = modifier,
        style = MyIconButtonDefaults.styleFilled(
            shape = shape,
            colors = colors
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyFilledTonalIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    // content
    content: @Composable () -> Unit
) {
    MyIconButton(
        onClick = onClick,
        modifier = modifier,
        style = MyIconButtonDefaults.styleFilledTonal(
            shape = shape,
            colors = colors
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    iconTint: Color? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    // click
    onClick: () -> Unit,
) {
    MyIconButton(
        icon = icon,
        modifier = modifier,
        style = MyIconButtonDefaults.styleDefault(colors = colors),
        iconPaddingValues = iconPaddingValues,
        iconTint = iconTint,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick
    )
}

@Composable
fun MyOutlinedIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    iconTint: Color? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = IconButtonDefaults.outlinedShape,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled = true),
    // click
    onClick: () -> Unit,
) {
    MyIconButton(
        icon = icon,
        modifier = modifier,
        style = MyIconButtonDefaults.styleOutlined(
            shape = shape,
            colors = colors,
            border = border
        ),
        iconPaddingValues = iconPaddingValues,
        iconTint = iconTint,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick
    )
}

@Composable
fun MyFilledIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    iconTint: Color? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    // click
    onClick: () -> Unit,
) {
    MyIconButton(
        icon = icon,
        modifier = modifier,
        style = MyIconButtonDefaults.styleFilled(
            shape = shape,
            colors = colors
        ),
        iconPaddingValues = iconPaddingValues,
        iconTint = iconTint,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick
    )
}

@Composable
fun MyFilledTonalIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    iconTint: Color? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    // click
    onClick: () -> Unit,
) {
    MyIconButton(
        icon = icon,
        modifier = modifier,
        style = MyIconButtonDefaults.styleFilledTonal(
            shape = shape,
            colors = colors
        ),
        iconPaddingValues = iconPaddingValues,
        iconTint = iconTint,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick
    )
}


// -------------------------
// internal API
// -------------------------

@Composable
private fun MyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MyIconButton.Style = MyIconButtonDefaults.styleDefault(),
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
private fun MyIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    style: MyIconButton.Style = MyIconButtonDefaults.styleDefault(),
    iconPaddingValues: PaddingValues = PaddingValues(),
    iconTint: Color? = null,
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
            tint = iconTint ?: LocalContentColor.current
        )
    }
}