package com.michaelflisar.toolbox.app.features.debug

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.AppSetup

private const val DEBUG_TAG = "DEBUG"

private fun isEnabled() = AppSetup.get().isDebugBuild

@Composable
fun Modifier.debugBorder(
    color: Color = Color.Red,
    width: Dp = 1.dp,
) = if (isEnabled()) {
    this.then(
        Modifier.border(
            width = width,
            color = color,
            shape = StyledTheme.shapes.large,
        ),
    )
} else {
    this
}

@Composable
fun DebugBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!isEnabled()) {
        content()
    } else {
        Box(
            modifier.debugBorder()
        ) {
            content()
        }
    }
}

@Composable
fun DebugObserveState(vararg values: Any?) {
    if (!isEnabled()) return

    LaunchedEffect(*values) {
        debugLog("STATE") {
            "Observed values changed: ${values.joinToString()}"
        }
    }
}

fun debugLog(message: () -> String) {
    L.logIf { isEnabled() }?.tag(DEBUG_TAG)?.d { message() }
}

fun debugLog(tag: String, message: () -> String) {
    L.logIf { isEnabled() }?.tag("$DEBUG_TAG-$tag")?.d { message() }
}
