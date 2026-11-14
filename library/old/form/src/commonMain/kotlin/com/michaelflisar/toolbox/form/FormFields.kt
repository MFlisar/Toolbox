package com.michaelflisar.toolbox.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

class FormFields(
    val fields: List<FormField<*>>,
    val isValid: State<Boolean>
)

@Composable
fun rememberFormFields(
    fields: List<FormField<*>>
): FormFields {
    val isValid = remember {
        derivedStateOf {
            fields.all { it.stateInfo.value.isEmpty() }
        }
    }
    return FormFields(fields, isValid)
}

