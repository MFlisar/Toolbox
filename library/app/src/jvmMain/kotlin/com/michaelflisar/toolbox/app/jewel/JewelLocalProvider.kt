package com.michaelflisar.toolbox.app.jewel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.michaelflisar.toolbox.app.features.appstate.JewelAppState
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState

@Composable
fun JewelLocalProvider(
    jewelAppState: JewelAppState,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalJewelAppState provides jewelAppState
    ) {
        content()
    }
}