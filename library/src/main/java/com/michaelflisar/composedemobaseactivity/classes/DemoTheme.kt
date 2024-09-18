package com.michaelflisar.composedemobaseactivity.classes

data class DemoTheme<T>(
    val theme: Theme,
    val colorScheme: T,
    val dynamic: Boolean
) {
    enum class Theme {
        Dark, Light, System
    }
}