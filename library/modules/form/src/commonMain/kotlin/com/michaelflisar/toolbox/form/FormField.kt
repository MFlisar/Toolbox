package com.michaelflisar.toolbox.form

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState

abstract class FormField<T> {

    abstract val label: String
    abstract val stateValue: MutableState<T>
    abstract val stateInfo: MutableState<String>
    abstract val isValid: (value: T) -> String

    val value: T
        get() = stateValue.value

    @Composable
    abstract fun render()

    @Composable
    fun renderState() {
        LaunchedEffect(stateValue.value) {
            stateInfo.value = isValid(stateValue.value)
        }
        if (stateInfo.value.isNotEmpty()) {
            Text(text = stateInfo.value, color = MaterialTheme.colorScheme.error)
        }
    }
}