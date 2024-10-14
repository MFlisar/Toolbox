package com.michaelflisar.toolbox.composables

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.michaelflisar.toolbox.cursor
import java.awt.Cursor

@Composable
fun MyInput(
    modifier: Modifier = Modifier,
    title: String = "",
    value: MutableState<String>,
    readOnly: Boolean = false
) {
    MyInput(modifier, title, value.value, readOnly) {
        value.value = it
    }
}

@Composable
fun MyInput(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
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

@Composable
fun MyInputInt(
    modifier: Modifier = Modifier,
    title: String,
    value: MutableState<Int>,
    readOnly: Boolean = false
) {
    MyInputInt(modifier, title, value.value, readOnly) {
        value.value = it
    }
}

@Composable
fun MyInputInt(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    readOnly: Boolean = false,
    onValueChange: (Int) -> Unit = {}
) {

    val text = remember { mutableStateOf(value.toString()) }
    OutlinedTextField(
        modifier = modifier,
        value = text.value,
        onValueChange = {
            text.value = it
            it.toIntOrNull()?.let {
                onValueChange(it)
            }
        },
        label = { Text(title) },
        readOnly = readOnly,
        isError = text.value.toIntOrNull() == null
    )
}

