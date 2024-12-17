package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import com.michaelflisar.toolbox.cursor

@Composable
fun MyInput(
    modifier: Modifier = Modifier,
    title: String = "",
    value: MutableState<String>,
    lines: Int = 1,
    readOnly: Boolean = false
) {
    MyInput(modifier, title, value.value, lines, readOnly) {
        value.value = it
    }
}

@Composable
fun MyInput(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String,
    lines: Int = 1,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = lines == 1,
        minLines = lines,
        maxLines = lines,
        label = if (title.isNotEmpty()) {
            { Text(title) }
        } else null,
        readOnly = readOnly,
        trailingIcon = if (value.isNotEmpty()) {
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
        } else null
    )
}