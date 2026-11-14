package com.michaelflisar.toolbox.form.fields

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.form.FormField

class FormFieldCheckbox internal constructor(
    override val label: String,
    override val stateValue: MutableState<Boolean>,
    override val stateInfo: MutableState<String>,
    override val isValid: (value: Boolean) -> String,
) : FormField<Boolean>() {

    @Composable
    override fun render() {
        MyCheckbox(title = "", checked = stateValue)
    }
}

@Composable
fun rememberFormFieldCheckbox(
    label: String,
    value: Boolean,
    isValid: (value: Boolean) -> String = { "" },
) = FormFieldCheckbox(
    label = label,
    stateValue = remember(value) { mutableStateOf(value) },
    stateInfo = remember(value) { mutableStateOf(isValid(value)) },
    isValid = isValid
)