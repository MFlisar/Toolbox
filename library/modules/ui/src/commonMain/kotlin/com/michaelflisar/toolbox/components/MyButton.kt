package com.michaelflisar.toolbox.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private object MyButton {

    sealed class Style {

        @Composable
        internal abstract fun Button(
            onClick: () -> Unit,
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            interactionSource: MutableInteractionSource? = null,
            content: @Composable RowScope.() -> Unit,
        )

        class Default internal constructor(
            val shape: Shape,
            val colors: ButtonColors,
            val elevation: ButtonElevation?,
            val border: BorderStroke?,
            val contentPadding: PaddingValues,
        ) : Style() {

            @Composable
            override fun Button(
                onClick: () -> Unit,
                modifier: Modifier,
                enabled: Boolean,
                interactionSource: MutableInteractionSource?,
                content: @Composable RowScope.() -> Unit,
            ) {
                Button(
                    onClick,
                    modifier,
                    enabled,
                    shape,
                    colors,
                    elevation,
                    border,
                    contentPadding,
                    interactionSource,
                    content
                )
            }
        }

        class Outlined internal constructor(
            val shape: Shape,
            val colors: ButtonColors,
            val elevation: ButtonElevation?,
            val border: BorderStroke?,
            val contentPadding: PaddingValues,
        ) : Style() {
            @Composable
            override fun Button(
                onClick: () -> Unit,
                modifier: Modifier,
                enabled: Boolean,
                interactionSource: MutableInteractionSource?,
                content: @Composable RowScope.() -> Unit,
            ) {
                OutlinedButton(
                    onClick,
                    modifier,
                    enabled,
                    shape,
                    colors,
                    elevation,
                    border,
                    contentPadding,
                    interactionSource,
                    content
                )
            }
        }

        class Text internal constructor(
            val shape: Shape,
            val colors: ButtonColors,
            val elevation: ButtonElevation?,
            val border: BorderStroke?,
            val contentPadding: PaddingValues,
        ) : Style() {
            @Composable
            override fun Button(
                onClick: () -> Unit,
                modifier: Modifier,
                enabled: Boolean,
                interactionSource: MutableInteractionSource?,
                content: @Composable RowScope.() -> Unit,
            ) {
                TextButton(
                    onClick,
                    modifier,
                    enabled,
                    shape,
                    colors,
                    elevation,
                    border,
                    contentPadding,
                    interactionSource,
                    content
                )
            }
        }

        class FilledTonal internal constructor(
            val shape: Shape,
            val colors: ButtonColors,
            val elevation: ButtonElevation?,
            val border: BorderStroke?,
            val contentPadding: PaddingValues,
        ) : Style() {
            @Composable
            override fun Button(
                onClick: () -> Unit,
                modifier: Modifier,
                enabled: Boolean,
                interactionSource: MutableInteractionSource?,
                content: @Composable RowScope.() -> Unit,
            ) {
                FilledTonalButton(
                    onClick,
                    modifier,
                    enabled,
                    shape,
                    colors,
                    elevation,
                    border,
                    contentPadding,
                    interactionSource,
                    content
                )
            }
        }
    }
}

private object MyButtonDefaults {

    @Composable
    fun styleDefault(
        shape: Shape = ButtonDefaults.shape,
        colors: ButtonColors = ButtonDefaults.buttonColors(),
        elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
        border: BorderStroke? = null,
        contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    ): MyButton.Style.Default {
        return MyButton.Style.Default(shape, colors, elevation, border, contentPadding)
    }

    @Composable
    fun styleOutlined(
        shape: Shape = ButtonDefaults.outlinedShape,
        colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
        elevation: ButtonElevation? = null,
        border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled = true),
        contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    ): MyButton.Style.Outlined {
        return MyButton.Style.Outlined(shape, colors, elevation, border, contentPadding)
    }

    @Composable
    fun styleText(
        shape: Shape = ButtonDefaults.textShape,
        colors: ButtonColors = ButtonDefaults.textButtonColors(),
        elevation: ButtonElevation? = null,
        border: BorderStroke? = null,
        contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    ): MyButton.Style.Text {
        return MyButton.Style.Text(shape, colors, elevation, border, contentPadding)
    }

    @Composable
    fun styleFilledTonal(
        shape: Shape = ButtonDefaults.filledTonalShape,
        colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
        elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
        border: BorderStroke? = null,
        contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    ): MyButton.Style.FilledTonal {
        return MyButton.Style.FilledTonal(shape, colors, elevation, border, contentPadding)
    }
}

// -------------------------
// public API
// -------------------------

@Composable
fun MyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    MyButton(
        onClick = onClick,
        style = MyButtonDefaults.styleDefault(shape, colors, elevation, border, contentPadding),
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled = true),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    MyButton(
        onClick = onClick,
        style = MyButtonDefaults.styleOutlined(shape, colors, elevation, border, contentPadding),
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = ButtonDefaults.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    MyButton(
        onClick = onClick,
        style = MyButtonDefaults.styleText(shape, colors, elevation, border, contentPadding),
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyFilledTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = ButtonDefaults.filledTonalShape,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    MyButton(
        onClick = onClick,
        style = MyButtonDefaults.styleFilledTonal(shape, colors, elevation, border, contentPadding),
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyButton(
    // icon + text
    text: String,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    // button
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    // click
    onClick: () -> Unit,
) {
    MyButton(
        onClick = onClick,
        style = MyButtonDefaults.styleDefault(shape, colors, elevation, border, contentPadding),
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        MyButtonContent(text, icon, iconRotation, iconTint)
    }
}

@Composable
fun MyOutlinedButton(
    // icon + text
    text: String,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    // button
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled = true),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    // click
    onClick: () -> Unit,
) {
    MyButton(
        onClick = onClick,
        style = MyButtonDefaults.styleOutlined(shape, colors, elevation, border, contentPadding),
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        MyButtonContent(text, icon, iconRotation, iconTint)
    }
}

@Composable
fun MyTextButton(
    // icon + text
    text: String,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    // button
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = ButtonDefaults.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    // click
    onClick: () -> Unit,
) {
    MyButton(
        onClick = onClick,
        style = MyButtonDefaults.styleText(shape, colors, elevation, border, contentPadding),
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        MyButtonContent(text, icon, iconRotation, iconTint)
    }
}

@Composable
fun MyFilledTonalButton(
    // icon + text
    text: String,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    // button
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = ButtonDefaults.filledTonalShape,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    // click
    onClick: () -> Unit,
) {
    MyButton(
        onClick = onClick,
        style = MyButtonDefaults.styleFilledTonal(shape, colors, elevation, border, contentPadding),
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        MyButtonContent(text, icon, iconRotation, iconTint)
    }
}

// -------------------------
// internal API
// -------------------------

@Composable
private fun MyButton(
    onClick: () -> Unit,
    modifier: Modifier,
    style: MyButton.Style,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    // content
    content: @Composable RowScope.() -> Unit,
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
private fun MyButtonContent(
    text: String,
    icon: ImageVector?,
    iconRotation: Float,
    iconTint: Color?
) {
    if (icon == null) {
        Text(text)
    } else {
        if (text.isEmpty()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp).rotate(iconRotation),
                tint = iconTint ?: LocalContentColor.current
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).rotate(iconRotation),
                    tint = iconTint ?: LocalContentColor.current
                )
                Text(text)
            }
        }

    }
}