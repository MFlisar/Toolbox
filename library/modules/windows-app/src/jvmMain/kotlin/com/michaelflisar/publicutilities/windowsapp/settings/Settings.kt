package com.michaelflisar.publicutilities.windowsapp.settings

import java.io.File

data class Settings(
    val file: File,
    private val settings: MutableList<SettingsItem<*>> = mutableListOf()
) {
    companion object {

        fun read(file: File): Settings {
            val settings = ArrayList<SettingsItem<*>>()
            if (file.exists()) {
                val lines = file.readLines()
                lines.filter { it.trim().isNotEmpty() }.forEach {
                    val parts = it.split(";")
                    val item = createItem(parts[0], parts[1], parts[2])
                    settings.add(item)
                }
            }
            return Settings(file, settings)
        }

        fun createItem(type: String, key: String, value: String): SettingsItem<*> {
            return when (type) {
                SettingsItem.Text::class.simpleName -> {
                    SettingsItem.Text(key, value)
                }

                SettingsItem.Bool::class.simpleName -> {
                    SettingsItem.Bool(key, value == "true")
                }

                SettingsItem.Integer::class.simpleName -> {
                    SettingsItem.Integer(key, value.toInt())
                }

                else -> throw RuntimeException("Type not handled!")
            }
        }

        fun <T, Item : SettingsItem<T>> createItem(setting: UISetting<T, Item>, value: T): SettingsItem<T> {
            val key = setting.id
            return when (setting) {
                is UISetting.Bool -> SettingsItem.Bool(key, value as Boolean)
                is UISetting.Integer -> SettingsItem.Integer(key, value as Int)
                is UISetting.List -> SettingsItem.Text(key, value as String)
                is UISetting.Text -> SettingsItem.Text(key, value as String)
            } as SettingsItem<T>
        }
    }

    fun <T, Item : SettingsItem<T>> getItem(setting: UISetting<T, Item>) =
        settings.find { it.key == setting.id } as? Item

    fun <T, Item : SettingsItem<T>> update(setting: UISetting<T, Item>, value: T): Item {
        val item = getItem(setting)
        return if (item != null) {
            item.state.value = value
            item
        } else {
            val item = createItem(setting, value) as Item
            settings.add(item)
            item
        }
    }

    fun write() {
        val text = settings.map { it.toLine() }.joinToString("\n")
        file.writeText(text, Charsets.UTF_8)
    }
}