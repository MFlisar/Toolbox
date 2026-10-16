package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.michaelflisar.toolbox.Components
import com.michaelflisar.toolbox.Platform.cursor
import com.michaelflisar.toolbox.components
import com.michaelflisar.toolbox.components.MyOutlinedDecoratedContainer.minimizedLabelHalfHeight
import com.michaelflisar.toolbox.padding

@Composable
fun MyInput(
    modifier: Modifier = Modifier,
    title: String = "",
    value: MutableState<String>,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    focusable: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    shape: Shape = MaterialTheme.components.textField.outlinedShape,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    MyInput(
        modifier = modifier,
        title = title,
        value = value.value,
        minLines = minLines,
        maxLines = maxLines,
        readOnly = readOnly,
        enabled = enabled,
        focusable = focusable,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        colors = colors,
        shape = shape,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource
    ) {
        value.value = it
    }
}

@Composable
fun MyInput(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    focusable: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    shape: Shape = MaterialTheme.components.textField.outlinedShape,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    showClearIcon: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onValueChange: (String) -> Unit = {},
) {
    when (MaterialTheme.components.style) {
        Components.Style.Custom -> {
            CustomOutlinedTextField(
                modifier = modifier
                    .focusProperties { canFocus = focusable },
                value = value,
                onValueChange = onValueChange,
                singleLine = minLines == 1 && maxLines == 1,
                minLines = minLines,
                maxLines = maxLines,
                label = if (title.isNotEmpty()) {
                    { Text(title) }
                } else null,
                readOnly = readOnly,
                enabled = enabled,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingClearIcon(
                    value = value,
                    showClearIcon = showClearIcon,
                    readOnly = readOnly,
                    enabled = enabled,
                    onValueChange = onValueChange
                ),
                colors = colors,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                shape = shape,
                interactionSource = interactionSource
            )

        }

        Components.Style.Material3 -> {
            OutlinedTextField(
                modifier = modifier.focusProperties { canFocus = focusable },
                value = value,
                onValueChange = onValueChange,
                singleLine = minLines == 1 && maxLines == 1,
                minLines = minLines,
                maxLines = maxLines,
                label = if (title.isNotEmpty()) {
                    { Text(title) }
                } else null,
                readOnly = readOnly,
                enabled = enabled,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingClearIcon(
                    value = value,
                    showClearIcon = showClearIcon,
                    readOnly = readOnly,
                    enabled = enabled,
                    onValueChange = onValueChange
                ),
                colors = colors,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                shape = shape,
                interactionSource = interactionSource
            )
        }
    }
}

@Composable
private fun trailingClearIcon(
    value: String,
    showClearIcon: Boolean,
    readOnly: Boolean,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
): @Composable (() -> Unit)? {
    return if (showClearIcon && !readOnly && enabled && !value.isEmpty()) {
        {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "clear",
                modifier = Modifier
                    .padding(end = MaterialTheme.padding.default)
                    .clip(CircleShape)
                    .cursor()
                    .focusProperties { canFocus = false }
                    .clickable {
                        onValueChange("")
                    }
                    .size(MaterialTheme.components.textField.trailingIconSize + MaterialTheme.components.textField.trailingIconPadding)
                    .padding(
                        MaterialTheme.components.textField.trailingIconPadding
                    )
            )
        }
    } else null
}

// -------------------------
// Custom Composables
// -------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    val style = MaterialTheme.components.textField

    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    // If color is not provided via the text style, use content color as a default
    val textColor =
        textStyle.color.takeOrElse {
            val focused = interactionSource.collectIsFocusedAsState().value
            when {
                !enabled -> colors.disabledTextColor
                isError -> colors.errorTextColor
                focused -> colors.focusedTextColor
                else -> colors.unfocusedTextColor
            }
        }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        BasicTextField(
            value = value,
            modifier =
                modifier
                    .then(
                        if (label != null) {
                            Modifier
                                // Merge semantics at the beginning of the modifier chain to ensure
                                // padding is considered part of the text field.
                                .semantics(mergeDescendants = true) {}
                                .padding(top = minimizedLabelHalfHeight())
                        } else {
                            Modifier
                        }
                    )
                    //.then(if (isError) Modifier.semantics() { error(getString(Strings.DefaultErrorMessage)) } else Modifier)
                    .defaultMinSize(
                        minWidth = style.minWidth,
                        minHeight = style.minHeight
                    ),
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(if (isError) colors.errorCursorColor else colors.cursorColor),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox =
                @Composable { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = value,
                        visualTransformation = visualTransformation,
                        innerTextField = innerTextField,
                        placeholder = placeholder,
                        label = label,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        prefix = prefix,
                        suffix = suffix,
                        supportingText = supportingText,
                        singleLine = singleLine,
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        contentPadding = style.contentPaddingWithoutLabel,
                        container = {
                            OutlinedTextFieldDefaults.Container(
                                enabled = enabled,
                                isError = isError,
                                interactionSource = interactionSource,
                                colors = colors,
                                shape = shape,
                            )
                        },
                    )
                },
        )
    }
}