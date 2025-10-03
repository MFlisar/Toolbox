package com.michaelflisar.toolbox.form2

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyInput

@Composable
fun FormScope.FormTextInput(
    title: String,
    value: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    maxLines: Int? = null,
    onValueChanged: (value: String) -> Unit,
) {
    val value = remember { mutableStateOf(value) }
    LaunchedEffect(Unit) {
        snapshotFlow { value.value }.collect { onValueChanged(it) }
    }
    FormTextInput(title, value, modifier, maxLines)
}

@Composable
fun FormScope.FormTextInput(
    title: String,
    value: MutableState<String>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    maxLines: Int? = null,
) {
    FormItem(
        label = title,
        modifier = modifier
    ) {
        MyInput(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            title = "",
            //trailingIcon = { TextField.TextFieldClearEndIcon(value) },
            maxLines = maxLines ?: Int.MAX_VALUE
        )
    }
}
/*
@PreviewCurrent
@Composable
fun PreviewFormTextInput() {
    IronPreviewTheme {
        val selected = remember {
            mutableStateOf("Hallo")
        }
        FormContainer {
            FormTextInput(
                title = "Text",
                value = selected.value
            ) {
                selected.value = it
                // TODO: persist...
            }
        }
    }
}*/