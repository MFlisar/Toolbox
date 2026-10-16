package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInputButton(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String,
    minLines: Int = 1,
    maxLines: Int = 1,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        MyInput(
            modifier = Modifier,
            title = title,
            value = value,
            minLines = minLines,
            maxLines = maxLines,
            enabled = true,
            shape = MaterialTheme.components.textField.outlinedShape,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            colors = colors,
            showClearIcon = false
        )

        Box(
            Modifier
                .matchParentSize()
                .padding(
                    top = if (title.isNotEmpty()) {
                        MyOutlinedDecoratedContainer.minimizedLabelHalfHeight()
                    } else {
                        0.dp
                    }
                )
                .clip(MaterialTheme.components.textField.outlinedShape)
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick
                )
        )
    }
}