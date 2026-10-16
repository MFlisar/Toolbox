package com.michaelflisar.toolbox.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.Components
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.components
import com.michaelflisar.toolbox.internal.ProvideContentColorTextStyle

internal object MyButton {

    @Stable
    data class Style(
        val shape: Shape,
        val colors: ButtonColors,
        val elevation: ButtonElevation?,
        val border: BorderStroke?,
        val contentPadding: PaddingValues,
    )

}

internal object MyButtonDefaults {

    @Composable
    fun styleDefault(
        shape: Shape = MaterialTheme.components.button.shape,
        colors: ButtonColors = ButtonDefaults.buttonColors(),
        elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
        border: BorderStroke? = null,
        contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    ): MyButton.Style {
        return MyButton.Style(shape, colors, elevation, border, contentPadding)
    }

    @Composable
    fun styleOutlined(
        shape: Shape = MaterialTheme.components.button.outlinedShape,
        colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
        elevation: ButtonElevation? = null,
        border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled = true),
        contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    ): MyButton.Style {
        return MyButton.Style(shape, colors, elevation, border, contentPadding)
    }

    @Composable
    fun styleText(
        shape: Shape = MaterialTheme.components.button.textShape,
        colors: ButtonColors = ButtonDefaults.textButtonColors(),
        elevation: ButtonElevation? = null,
        border: BorderStroke? = null,
        contentPadding: PaddingValues = MaterialTheme.components.button.textContentPadding,
    ): MyButton.Style {
        return MyButton.Style(shape, colors, elevation, border, contentPadding)
    }

    @Composable
    fun styleFilledTonal(
        shape: Shape = MaterialTheme.components.button.filledTonalShape,
        colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
        elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
        border: BorderStroke? = null,
        contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    ): MyButton.Style {
        return MyButton.Style(shape, colors, elevation, border, contentPadding)
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
    shape: Shape = MaterialTheme.components.button.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    MyButtonImpl(
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
    shape: Shape = MaterialTheme.components.button.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled = true),
    contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    MyButtonImpl(
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
    shape: Shape = MaterialTheme.components.button.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = MaterialTheme.components.button.textContentPadding,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    MyButtonImpl(
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
    shape: Shape = MaterialTheme.components.button.filledTonalShape,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    MyButtonImpl(
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
    icon: IconComposable? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    // button
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = MaterialTheme.components.button.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    // click
    onClick: () -> Unit,
) {
    MyButtonImpl(
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
    icon: IconComposable? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    // button
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = MaterialTheme.components.button.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled = true),
    contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    // click
    onClick: () -> Unit,
) {
    MyButtonImpl(
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
    icon: IconComposable? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    // button
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = MaterialTheme.components.button.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = MaterialTheme.components.button.textContentPadding,
    // click
    onClick: () -> Unit,
) {
    MyButtonImpl(
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
    icon: IconComposable? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    // button
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = MaterialTheme.components.button.filledTonalShape,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = MaterialTheme.components.button.contentPadding,
    // click
    onClick: () -> Unit,
) {
    MyButtonImpl(
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
internal fun MyButtonImpl(
    onClick: () -> Unit,
    modifier: Modifier,
    style: MyButton.Style,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    val componentStyles = MaterialTheme.components.style
    when (componentStyles) {
        Components.Style.Custom -> {
            CustomButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = style.shape,
                colors = style.colors,
                elevation = style.elevation,
                border = style.border,
                contentPadding = style.contentPadding,
                interactionSource = interactionSource,
                content = content
            )
        }

        Components.Style.Material3 -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = style.shape,
                colors = style.colors,
                elevation = style.elevation,
                border = style.border,
                contentPadding = style.contentPadding,
                interactionSource = interactionSource,
                content = content
            )
        }
    }
}

@Composable
private fun MyButtonContent(
    text: String,
    icon: IconComposable?,
    iconRotation: Float,
    iconTint: Color?,
) {
    if (icon == null) {
        Text(text)
    } else {
        if (text.isEmpty()) {
            com.michaelflisar.toolbox.extensions.Icon(
                icon = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(MaterialTheme.components.icons.size)
                    .rotate(iconRotation),
                tint = iconTint ?: LocalContentColor.current
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                com.michaelflisar.toolbox.extensions.Icon(
                    icon = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).rotate(iconRotation),
                    tint = iconTint ?: LocalContentColor.current
                )
                Text(text)
            }
        }

    }
}

// -------------------------
// Custom Composables
// -------------------------

/**
 * Elevation wird ignoriert!
 */
@Composable
private fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape,
    colors: ButtonColors,
    elevation: ButtonElevation?,
    border: BorderStroke?,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val style = MaterialTheme.components.button

    val source = interactionSource ?: remember { MutableInteractionSource() }

    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    val shadowElevation = 0.dp // elevation?.shadowElevation(enabled, source)?.value ?: 0.dp

    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = shadowElevation,
        border = border,
        interactionSource = source,
    ) {
        ProvideContentColorTextStyle(
            contentColor = contentColor,
            textStyle = MaterialTheme.typography.labelLarge,
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = style.minWidth,
                        minHeight = style.minHeight,
                    )
                    .padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}