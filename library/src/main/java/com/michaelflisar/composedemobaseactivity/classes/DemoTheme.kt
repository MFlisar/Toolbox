package com.michaelflisar.composedemobaseactivity.classes

data class DemoTheme<T>(
    val baseTheme: T,
    val colorScheme: String,
    val dynamic: Boolean,
    val availableColorSchemes: List<String>
)