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
    val flow = this
    val currentValue by flow.collectAsStateWithLifecycle()
    val localState = remember(flow) { mutableStateOf(currentValue) }

    LaunchedEffect(flow, currentValue) {
        val latestValue = flow.value
        if (localState.value != latestValue) {
            localState.value = latestValue
        }
    }

    return remember(flow, localState) {
        object : MutableState<T> {
            override var value: T
                get() = localState.value
                set(newValue) {
                    localState.value = newValue
                    flow.value = newValue
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}