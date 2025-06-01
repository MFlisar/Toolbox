package com.michaelflisar.toolbox.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyNumericInput

sealed class FormField<T> {

    abstract val label: String
    abstract val value: MutableState<T>
    abstract val state: MutableState<String>
    abstract val isValid: (value: T) -> String

    @Composable
    abstract fun render()

    @Composable
    fun renderState() {
        LaunchedEffect(value.value) {
            state.value = isValid(value.value)
        }
        if (state.value.isNotEmpty()) {
            Text(text = state.value, color = MaterialTheme.colorScheme.error)
        }
    }

    class Info internal constructor(
        override val label: String,
        val info: String,
        override val state: MutableState<String>
    ) : FormField<String>() {

        @Composable
        override fun render() {
            MyInput(title = "", value = info, readOnly = true, focusable = false, colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent
            ))
            //Text(info)
        }

        override val isValid: (String) -> String = { "" }

        override val value = mutableStateOf(info)
    }

    class Number<T : kotlin.Number?> internal constructor(
        override val label: String,
        override val value: MutableState<T>,
        override val state: MutableState<String>,
        override val isValid: (value: T) -> String
    ) : FormField<T>() {

        @Composable
        override fun render() {
            MyNumericInput(
                title = "",
                value = value as MutableState<T?>,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    class Text internal constructor(
        override val label: String,
        override val value: MutableState<String>,
        override val state: MutableState<String>,
        override val isValid: (value: String) -> String
    ) : FormField<String>() {

        @Composable
        override fun render() {
            MyInput(title = "", value = value)
        }
    }

    class Checkbox internal constructor(
        override val label: String,
        override val value: MutableState<Boolean>,
        override val state: MutableState<String>,
        override val isValid: (value: Boolean) -> String
    ) : FormField<Boolean>() {

        @Composable
        override fun render() {
            MyCheckbox(title = "", checked = value)
        }
    }

    class Dropdown internal constructor(
        override val label: String,
        override val value: MutableState<Int>,
        override val state: MutableState<String>,
        val items: List<String>,
        override val isValid: (value: Int) -> String
    ) : FormField<Int>() {

        @Composable
        override fun render() {
            MyDropdown(title = "", items = items, selected = value)
        }
    }

    class Customs<T> internal constructor(
        override val label: String,
        override val value: MutableState<T>,
        override val state: MutableState<String>,
        override val isValid: (value: T) -> String,
        val content: @Composable (value: T) -> Unit
    ) : FormField<T>() {

        @Composable
        override fun render() {
            content(value.value)
        }
    }
}

@Composable
fun rememberFormFieldInfo(
    label: String,
    info: String
) = FormField.Info(
    label = label,
    info = info,
    state = remember { mutableStateOf("") }
)

@Composable
fun <T : Number?> rememberFormFieldNumber(
    label: String,
    value: T,
    isValid: (value: T?) -> String = { "" }
) = FormField.Number(
    label = label,
    value = remember(value) { mutableStateOf(value) },
    state = remember(value) { mutableStateOf(isValid(value)) },
    isValid = isValid
)

@Composable
fun rememberFormFieldText(
    label: String,
    value: String,
    isValid: (value: String) -> String = { "" }
) = FormField.Text(
    label = label,
    value = remember(value) { mutableStateOf(value) },
    state = remember(value) { mutableStateOf(isValid(value)) },
    isValid = isValid
)

@Composable
fun rememberFormFieldCheckbox(
    label: String,
    value: Boolean,
    isValid: (value: Boolean) -> String = { "" }
) = FormField.Checkbox(
    label = label,
    value = remember(value) { mutableStateOf(value) },
    state = remember(value) { mutableStateOf(isValid(value)) },
    isValid = isValid
)

@Composable
fun rememberFormFieldDropdown(
    label: String,
    selected: Int,
    items: List<String>,
    isValid: (value: Int) -> String = { "" }
) = FormField.Dropdown(
    label = label,
    value = remember(selected) { mutableStateOf(selected) },
    state = remember(selected) { mutableStateOf(isValid(selected)) },
    items = items,
    isValid = isValid
)

@Composable
fun <T> rememberFormFieldCustom(
    label: String,
    value: T,
    isValid: (value: T) -> String = { "" },
    content: @Composable (value: T) -> Unit
) = FormField.Customs(
    label = label,
    value = remember(value) { mutableStateOf(value) },
    state = remember(value) { mutableStateOf(isValid(value)) },
    isValid = isValid,
    content = content
)