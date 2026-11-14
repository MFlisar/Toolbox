package com.michaelflisar.toolbox.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.michaelflisar.toolbox.extensions.toggle

@Stable
class Expanded<T>(
    val expanded: MutableState<Set<T>>,
    val singleOnly: MutableState<Boolean>
) {
    fun isExpanded(value: T) = expanded.value.contains(value)

    fun toggle(value: T) {
        expanded.value = expanded.value.toggle(value, forceSingle = singleOnly.value)
    }
}

@Composable
fun <T: Any> rememberExpanded(
    expanded: Set<T> = emptySet(),
    singleOnly: Boolean
) = Expanded(
    expanded = rememberSaveable(saver = saverMutableStateSet()) { mutableStateOf(expanded) },
    singleOnly = rememberSaveable { mutableStateOf(singleOnly) },
)