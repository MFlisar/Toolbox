package com.michaelflisar.toolbox.form.fields

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.form.FormField

class FormFieldText internal constructor(
    override val label: String,
    override val stateValue: MutableState<String>,
    override val stateInfo: MutableState<String>,
    override val isValid: (value: String) -> String,
) : FormField<String>() {

    @Composable
    override fun render() {
        MyInput(title = "", value = stateValue)
    }
}

@Composable
fun rememberFormFieldText(
    label: String,
    value: String,
    isValid: (value: String) -> String = { "" },
) = FormFieldText(
    label = label,
    stateValue = remember(value) { mutableStateOf(value) },
    stateInfo = remember(value) { mutableStateOf(isValid(value)) },
    isValid = isValid
)