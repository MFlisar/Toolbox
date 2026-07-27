package com.michaelflisar.toolbox.app.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun <T> MutableStateFlow<T>.asMutableState(): MutableState<T> {
    // local state to be able to change the value IMMEDIATELY
    val localState = remember { mutableStateOf(value) }
    // synchronise local state in case the state changes from outside
    val currentValue by collectAsStateWithLifecycle()
    LaunchedEffect(currentValue) {
        if (localState.value != currentValue) {
            localState.value = currentValue
        }
    }
    return object : MutableState<T> {
        override var value: T
            get() = localState.value
            set(newValue) {
                localState.value = newValue
                this@asMutableState.value = newValue
            }

        override fun component1() = value
        override fun component2() = { v: T -> value = v }
    }
}