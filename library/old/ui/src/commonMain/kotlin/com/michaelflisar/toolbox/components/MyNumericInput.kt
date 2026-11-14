package com.michaelflisar.toolbox.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.michaelflisar.toolbox.numbers.NumberUtil

@Composable
fun <T : Number> MyNumericInput(
    value: MutableState<T>,
    modifier: Modifier = Modifier,
    valid: MutableState<Boolean> = remember { mutableStateOf(true) },
    defaultValue: T = NumberUtil.zero(value.value),
    title: String = "",
    readOnly: Boolean = false,
    enabled: Boolean = true,
    showClearIcon: Boolean = true,
) {
    MyNumericInput(
        value = value.value,
        modifier = modifier,
        defaultValue = defaultValue,
        title = title,
        readOnly = readOnly,
        enabled = enabled,
        showClearIcon = showClearIcon
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
    readOnly: Boolean = false,
    enabled: Boolean = true,
    showClearIcon: Boolean = true,
) {
    MyNumericInputNullable(
        value = value.value,
        modifier = modifier,
        instance = instance,
        title = title,
        readOnly = readOnly,
        enabled = enabled,
        showClearIcon = showClearIcon
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
    enabled: Boolean = true,
    showClearIcon: Boolean = true,
    onValueChange: (value: T?) -> Unit = {},
) {
    val text = remember { mutableStateOf(value.toString()) }
    val supportsDecimal = remember { NumberUtil.supportsDecimal(defaultValue) }

    MyInput(
        modifier = modifier,
        value = text.value,
        onValueChange = {
            val filtered = NumberUtil.clearText(it, supportsDecimal)
            text.value = filtered
            val value = NumberUtil.parse(defaultValue, filtered)
            onValueChange(value)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (supportsDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
        title = title,
        readOnly = readOnly,
        enabled = enabled,
        showClearIcon = showClearIcon,
        minLines = 1,
        maxLines = 1
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
    enabled: Boolean = true,
    showClearIcon: Boolean = true,
    onValueChange: (value: T?) -> Unit = {},
) {
    val text = remember { mutableStateOf(value?.toString() ?: "") }
    val supportsDecimal = remember { NumberUtil.supportsDecimal(instance) }

    MyInput(
        modifier = modifier,
        value = text.value,
        onValueChange = {
            val filtered = NumberUtil.clearText(it, supportsDecimal)
            text.value = filtered
            val value = NumberUtil.parse(instance, filtered)
            onValueChange(value)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (supportsDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
        title = title,
        readOnly = readOnly,
        enabled = enabled,
        showClearIcon = showClearIcon,
        minLines = 1,
        maxLines = 1
    )
}