package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import com.michaelflisar.toolbox.Platform.cursor
import com.michaelflisar.toolbox.numbers.NumberUtil

@Composable
fun <T : Number> MyNumericInput(
    value: MutableState<T>,
    modifier: Modifier = Modifier,
    valid: MutableState<Boolean> = remember { mutableStateOf(true) },
    defaultValue: T = NumberUtil.zero(value.value),
    title: String = "",
    readOnly: Boolean = false
) {
    MyNumericInput(
        value = value.value,
        modifier = modifier,
        defaultValue = defaultValue,
        title = title,
        readOnly = readOnly
    ) {
        valid.value = it != null
        value.value = it ?: defaultValue
    }
}

@Composable
fun <T : Number> MyNumericInputNullableState(
    value: MutableState<T?>,
    instance: T,
    modifier: Modifier = Modifier,
    valid: MutableState<Boolean> = remember { mutableStateOf(true) },
    title: String = "",
    readOnly: Boolean = false
) {
    MyNumericInputNullable(
        value = value.value,
        modifier = modifier,
        instance = instance,
        title = title,
        readOnly = readOnly
    ) {
        valid.value = it != null
        value.value = it
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T : Number> MyNumericInput(
    value: T,
    modifier: Modifier = Modifier,
    defaultValue: T = NumberUtil.zero(value),
    title: String = "",
    readOnly: Boolean = false,
    onValueChanged: (value: T?) -> Unit = {},
) {
    val text = remember { mutableStateOf(value.toString()) }
    val supportsDecimal = remember { NumberUtil.supportsDecimal(defaultValue) }

    OutlinedTextField(
        modifier = modifier,
        value = text.value,
        onValueChange = {
            val filtered = NumberUtil.clearText(it, supportsDecimal)
            text.value = filtered
            val value = NumberUtil.parse(defaultValue, filtered)
            onValueChanged(value)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (supportsDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
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
                            onValueChanged(null)
                        }
                )
            }
        } else null,
        singleLine = true
    )
}


@Suppress("UNCHECKED_CAST")
@Composable
fun <T : Number> MyNumericInputNullable(
    value: T?,
    instance: T,
    modifier: Modifier = Modifier,
    title: String = "",
    readOnly: Boolean = false,
    onValueChanged: (value: T?) -> Unit = {},
) {
    val text = remember { mutableStateOf(value?.toString() ?: "") }
    val supportsDecimal = remember { NumberUtil.supportsDecimal(instance) }

    OutlinedTextField(
        modifier = modifier,
        value = text.value,
        onValueChange = {
            val filtered = NumberUtil.clearText(it, supportsDecimal)
            text.value = filtered
            val value = NumberUtil.parse(instance, filtered)
            onValueChanged(value)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (supportsDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
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
                            onValueChanged(null)
                        }
                )
            }
        } else null,
        singleLine = true
    )
}