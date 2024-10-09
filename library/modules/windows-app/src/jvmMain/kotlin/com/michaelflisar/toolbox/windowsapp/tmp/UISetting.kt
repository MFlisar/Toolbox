package com.michaelflisar.toolbox.windowsapp.tmp

import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting

sealed class UISetting<T>(
    val setting: StorageSetting<T>,
    val label: String
) {
    class Text(
        setting: StorageSetting<String>,
        label: String = setting.key,
        val defaultValue: String
    ) : UISetting<String>(setting, label)

    class List(
        setting: StorageSetting<String>,
        label: String = setting.key,
        val items: kotlin.collections.List<String>,
        val defaultIndex: Int = 0
    ) : UISetting<String>(setting, label)

    class Bool(
        setting: StorageSetting<Boolean>,
        label: String = setting.key,
        val defaultValue: Boolean = true
    ) : UISetting<Boolean>(setting, label)

    class Integer(
        setting: StorageSetting<Int>,
        label: String = setting.key,
        val defaultValue: Int = 0
    ) : UISetting<Int>(setting, label)
}