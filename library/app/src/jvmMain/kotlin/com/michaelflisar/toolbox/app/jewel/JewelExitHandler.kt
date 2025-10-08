package com.michaelflisar.toolbox.app.jewel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.ApplicationScope

@Composable
internal fun ApplicationScope.JewelExitHandler(
    appIsClosing: MutableState<Boolean>,
) {
    if (appIsClosing.value) {
        exitApplication()
    }
}