package com.michaelflisar.toolbox.form.fields

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.form.FormField

class FormFieldCustoms<T> internal constructor(
    override val label: String,
    override val stateValue: MutableState<T>,
    override val stateInfo: MutableState<String>,
    override val isValid: (value: T) -> String,
    val content: @Composable (value: T) -> Unit,
) : FormField<T>() {

    @Composable
    override fun render() {
        content(stateValue.value)
    }
}

@Composable
fun <T> rememberFormFieldCustom(
    label: String,
    value: T,
    isValid: (value: T) -> String = { "" },
    content: @Composable (value: T) -> Unit,
) = FormFieldCustoms(
    label = label,
    stateValue = remember(value) { mutableStateOf(value) },
    stateInfo = remember(value) { mutableStateOf(isValid(value)) },
    isValid = isValid,
    content = content
)