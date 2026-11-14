package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import kotlinx.browser.document

@Composable
fun WasmApplication(
    content: @Composable () -> Unit,
) {
    // remove loading element
    document.getElementById(WasmAppSetup.get().divLoadingElementId)?.remove()

    content()
}

