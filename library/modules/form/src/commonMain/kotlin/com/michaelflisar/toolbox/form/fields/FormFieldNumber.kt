package com.michaelflisar.toolbox.form.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyNumericInput
import com.michaelflisar.toolbox.form.FormField

class FormFieldNumber<T : Number?> internal constructor(
    override val label: String,
    override val stateValue: MutableState<T>,
    override val stateInfo: MutableState<String>,
    override val isValid: (value: T) -> String,
) : FormField<T>() {

    @Composable
    override fun render() {
        MyNumericInput(
            title = "",
            value = stateValue.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChanged = { it?.let { stateValue.value = it } },
        )
    }
}

@Composable
fun <T : Number?> rememberFormFieldNumber(
    label: String,
    value: T,
    isValid: (value: T?) -> String = { "" },
) = FormFieldNumber(
    label = label,
    stateValue = remember(value) { mutableStateOf(value) },
    stateInfo = remember(value) { mutableStateOf(isValid(value)) },
    isValid = isValid
)