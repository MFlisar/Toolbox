package com.michaelflisar.toolbox.form.fields

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.form.FormField

class FormFieldInfo internal constructor(
    override val label: String,
    val info: String,
    override val stateInfo: MutableState<String>,
) : FormField<String>() {

    @Composable
    override fun render() {
        MyInput(
            title = "",
            value = info,
            readOnly = true,
            focusable = false,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Companion.Transparent
            )
        )
        //Text(info)
    }

    override val isValid: (String) -> String = { "" }

    override val stateValue = mutableStateOf(info)
}

@Composable
fun rememberFormFieldInfo(
    label: String,
    info: String,
) = FormFieldInfo(
    label = label,
    info = info,
    stateInfo = remember { mutableStateOf("") }
)