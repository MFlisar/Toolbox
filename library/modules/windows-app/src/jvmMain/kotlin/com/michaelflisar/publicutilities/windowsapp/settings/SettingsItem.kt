package com.michaelflisar.publicutilities.windowsapp.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class SettingsItem<T> {

    abstract val key: String
    abstract val state: MutableState<T>
    abstract fun getValueString(): String

    fun toLine() = "${this::class.simpleName};${key};${getValueString()}"

    class Text(override val key: String, value: String) : SettingsItem<String>() {
        override val state = mutableStateOf(value)
        override fun getValueString() = state.value
    }

    class Bool(override val key: String, value: Boolean) : SettingsItem<Boolean>() {
        override val state = mutableStateOf(value)
        override fun getValueString() = state.value.let { if (it) "true" else "false" }
    }

    class Integer(override val key: String, value: Int) : SettingsItem<Int>() {
        override val state = mutableStateOf(value)
        override fun getValueString() = state.value.toString()
    }
}