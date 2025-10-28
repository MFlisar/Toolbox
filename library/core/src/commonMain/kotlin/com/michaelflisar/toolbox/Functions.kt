package com.michaelflisar.toolbox

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle

@Composable
fun ProvideTextStyle(
    style: TextStyle = LocalTextStyle.current,
    mergeWith: TextStyle,
    content: @Composable () -> Unit
) {
    val mergedStyle = style.merge(mergeWith)
    CompositionLocalProvider(LocalTextStyle provides mergedStyle, content = content)
}