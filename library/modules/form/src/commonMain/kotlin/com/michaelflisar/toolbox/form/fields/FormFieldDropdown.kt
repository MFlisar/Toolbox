package com.michaelflisar.toolbox.form.fields

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.form.FormField

class FormFieldDropdown internal constructor(
    override val label: String,
    override val stateValue: MutableState<Int>,
    override val stateInfo: MutableState<String>,
    val items: List<String>,
    override val isValid: (value: Int) -> String,
) : FormField<Int>() {

    @Composable
    override fun render() {
        MyDropdown(title = "", items = items, selectedIndex = stateValue)
    }
}

@Composable
fun rememberFormFieldDropdown(
    label: String,
    selected: Int,
    items: List<String>,
    isValid: (value: Int) -> String = { "" },
) = FormFieldDropdown(
    label = label,
    stateValue = remember(selected) { mutableStateOf(selected) },
    stateInfo = remember(selected) { mutableStateOf(isValid(selected)) },
    items = items,
    isValid = isValid
)