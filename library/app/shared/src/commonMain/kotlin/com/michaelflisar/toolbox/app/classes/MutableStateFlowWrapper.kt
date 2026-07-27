package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.michaelflisar.kotpreferences.compose.asStateFlowNotNull
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MutableStateFlowWrapper<T> internal constructor(
    initialValue: T,
    val scope: CoroutineScope
) {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<T> = _state

    fun update(value: T) {
        scope.launch {
            _state.emit(value)
        }
    }

    //@Composable
    //fun collectAsState() = state.collectAsState()

    @Composable
    fun collectAsStateWithLifecycle() = state.collectAsStateWithLifecycle()
}

fun <T> ScreenModel.mutableStateFlowWrapper(initialValue: T) =
    MutableStateFlowWrapper(initialValue, screenModelScope)

class MutableStateFlowSettingWrapper<T> internal constructor(
    val storageSetting: StorageSetting<T>,
    val scope: CoroutineScope
) {

    private val _state = storageSetting.asStateFlowNotNull(scope)
    val state: StateFlow<T> = _state

    fun update(value: T) {
        scope.launch {
            storageSetting.update(value)
        }
    }

    //@Composable
    //fun collectAsState() = state.collectAsState()

    @Composable
    fun collectAsStateWithLifecycle() = state.collectAsStateWithLifecycle()
}

fun <T> ScreenModel.mutableStateFlowWrapper(
    storageSetting: StorageSetting<T>
) = MutableStateFlowSettingWrapper(storageSetting, screenModelScope)