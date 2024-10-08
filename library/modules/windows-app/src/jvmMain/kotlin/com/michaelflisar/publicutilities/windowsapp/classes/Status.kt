package com.michaelflisar.publicutilities.windowsapp.classes

sealed class Status {
    data object None : Status()
    class Running(
        val label: String,
        val showProgress: Boolean = true,
        val singleLine: Boolean = true
    ) : Status()
}