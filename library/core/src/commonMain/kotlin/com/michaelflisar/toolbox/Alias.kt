package com.michaelflisar.toolbox

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

typealias IconComposable = @Composable (contentDescription: String?, modifier: Modifier, tint: Color) -> Unit