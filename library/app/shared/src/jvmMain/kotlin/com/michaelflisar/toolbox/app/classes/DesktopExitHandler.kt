package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.ApplicationScope

@Composable
fun ApplicationScope.DesktopExitHandler(
    appIsClosing: MutableState<Boolean>,
) {
    if (appIsClosing.value) {
        exitApplication()
    }
}