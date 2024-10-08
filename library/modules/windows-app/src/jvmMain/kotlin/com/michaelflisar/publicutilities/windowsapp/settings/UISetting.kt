package com.michaelflisar.publicutilities.windowsapp.settings

import androidx.compose.runtime.MutableState
import com.michaelflisar.publicutilities.windowsapp.classes.AppState

sealed class UISetting<T, Item : SettingsItem<T>>(
    val id: String,
    val label: String,
    val internal: Boolean
) {
    protected abstract fun getItem(settings: Settings): Item

    fun updateValue(appState: AppState, value: T) {
        updateValue(appState.settings, value)
    }

    private fun updateValue(settings: MutableState<Settings>, value: T) {
        settings.value.update(this, value)
    }

    fun getState(appState: AppState) = getState(appState.settings)

    private fun getState(settings: MutableState<Settings>) = getItem(settings.value).state

    class Text(
        id: String,
        label: String = id,
        val defaultValue: String,
        internal: Boolean = false
    ) : UISetting<String, SettingsItem.Text>(id, label, internal) {

        override fun getItem(settings: Settings): SettingsItem.Text {
            return settings.getItem(this) ?: settings.update(this, defaultValue)
        }
    }

    class List(
        id: String,
        label: String = id,
        val items: kotlin.collections.List<String>,
        val defaultIndex: Int = 0,
        internal: Boolean = false
    ) : UISetting<String, SettingsItem.Text>(id, label, internal) {

        override fun getItem(settings: Settings): SettingsItem.Text {
            return settings.getItem(this) ?: settings.update(this, items[defaultIndex])
        }
    }

    class Bool(
        id: String,
        label: String = id,
        val defaultValue: Boolean = true,
        internal: Boolean = false
    ) : UISetting<Boolean, SettingsItem.Bool>(id, label, internal) {
        override fun getItem(settings: Settings): SettingsItem.Bool {
            return settings.getItem(this) ?: settings.update(this, defaultValue)
        }
    }

    class Integer(
        id: String,
        label: String = id,
        val defaultValue: Int = 0,
        internal: Boolean = false
    ) : UISetting<Int, SettingsItem.Integer>(id, label, internal) {
        override fun getItem(settings: Settings): SettingsItem.Integer {
            return settings.getItem(this) ?: settings.update(this, defaultValue)
        }
    }
}