package com.michaelflisar.toolbox.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.michaelflisar.toolbox.Platform.cursor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInputButton(
    modifier: Modifier = Modifier.Companion,
    title: String = "",
    value: String,
    minLines: Int = 1,
    maxLines: Int = 1,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    onClick: () -> Unit
) {
    MyOutlinedDecoratedContainer(
        title = title,
        modifier = modifier,
        enabled = enabled,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        colors = colors,
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().then(MyOutlinedDecoratedContainer.MODIFIER_CORRECTION),
            text = value,
            style = LocalTextStyle.current,
            color = MaterialTheme.colorScheme.onSurface,
            minLines = minLines,
            maxLines = maxLines
        )
    }
}