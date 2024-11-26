package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.clickable
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
import com.michaelflisar.toolbox.cursor

@Composable
fun <T : Number> MyNumericInput(
    modifier: Modifier = Modifier,
    title: String,
    value: MutableState<T?>,
    readOnly: Boolean = false
) {
    MyNumericInput(modifier, title, value.value, readOnly) {
        value.value = it
    }
}

@Composable
fun <T : Number> MyNumericInput(
    modifier: Modifier = Modifier,
    title: String,
    value: T?,
    readOnly: Boolean = false,
    onValueChange: (T?) -> Unit = {}
) {
    val text = remember { mutableStateOf(value?.toString() ?: "") }
    OutlinedTextField(
        modifier = modifier,
        value = text.value,
        onValueChange = {
            text.value = it
            when (value) {
                is Int? -> onValueChange(it.toIntOrNull() as T?)
                is Double? -> onValueChange(it.toDoubleOrNull() as T?)
                is Float? -> onValueChange(it.toFloatOrNull() as T?)
                is Long? -> onValueChange(it.toLongOrNull() as T?)
            }
        },
        label = { Text(title) },
        readOnly = readOnly,
        //isError = text.value.toIntOrNull() == null
        trailingIcon = if (text.value.isNotEmpty()) {
            {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "clear",
                    modifier = Modifier
                        .clip(CircleShape)
                        .cursor()
                        .focusProperties { canFocus = false }
                        .clickable {
                            text.value = ""
                            onValueChange(null)
                        }
                )
            }
        } else null
    )
}