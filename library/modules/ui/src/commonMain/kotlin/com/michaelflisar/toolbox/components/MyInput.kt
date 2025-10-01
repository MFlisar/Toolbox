package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import com.michaelflisar.toolbox.Platform.cursor

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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    MyInput(
        modifier,
        title,
        value.value,
        minLines,
        maxLines,
        readOnly,
        enabled,
        focusable,
        placeholder,
        leadingIcon,
        colors,
        keyboardOptions,
        keyboardActions
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit = {}
) {
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
        trailingIcon = if (!readOnly && enabled && value.isNotEmpty()) {
            {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "clear",
                    modifier = Modifier
                        .clip(CircleShape)
                        .cursor()
                        .focusProperties { canFocus = false }
                        .clickable {
                            onValueChange("")
                        }
                )
            }
        } else null,
        colors = colors,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

