package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import kotlinx.browser.document

@Composable
fun WasmApplication(
    content: @Composable () -> Unit,
) {
    HideLoading(WasmAppSetup.get().loadingDivId)
    content()
}

@Composable
private fun HideLoading(id: String) {
    LaunchedEffect(id) {
        document.getElementById(id)
            ?.setAttribute("style", "display:none")
    }
}