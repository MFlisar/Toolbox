package com.michaelflisar.toolbox.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michaelflisar.toolbox.Platform.cursor

object MyOutlinedDecoratedContainer {
    // TODO: find out why extra padding is necessary + fix it
    val MODIFIER_CORRECTION = Modifier.padding(vertical = 1.dp)

    @Composable
    internal fun minimizedLabelHalfHeight(): Dp {
        val compositionLocalValue = MaterialTheme.typography.bodySmall.lineHeight
        val fallbackValue = 16.sp//TypeScaleTokens.BodySmallLineHeight
        val value = if (compositionLocalValue.isSp) compositionLocalValue else fallbackValue
        return with(LocalDensity.current) { value.toDp() / 2 }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOutlinedDecoratedContainer(
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
    minHeight: Dp = OutlinedTextFieldDefaults.MinHeight,
    minWidth: Dp = OutlinedTextFieldDefaults.MinWidth,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .then(
                if (title.isNotEmpty()) {
                    Modifier
                        // Merge semantics at the beginning of the modifier chain to ensure
                        // padding is considered part of the text field.
                        .semantics(mergeDescendants = true) {}
                        .padding(top = MyOutlinedDecoratedContainer.minimizedLabelHalfHeight())
                } else {
                    Modifier
                }
            )
            .defaultMinSize(
                minWidth = minWidth,
                minHeight = minHeight,
            ),
    ) {
        OutlinedTextFieldDefaults.DecorationBox(
            value = title,
            visualTransformation = VisualTransformation.None,
            innerTextField = {
                content()
            },
            label = if (title.isNotEmpty()) {
                { Text(title) }
            } else null,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = true,
            enabled = enabled,
            isError = false,
            interactionSource = interactionSource,
            colors = colors,
            contentPadding = contentPadding,
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = enabled,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors,
                    shape = shape,
                    modifier = Modifier.then(
                        if (enabled && onClick != null) {
                            Modifier
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current,
                                    enabled = enabled
                                ) { onClick() }
                                .cursor()
                        } else Modifier
                    )
                )
            }
        )
    }
}