package com.michaelflisar.toolbox.ui.adaptive.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledRegistry

@Stable
data class StyledThemeDefinition(
    val name: String,
    val registry: StyledRegistry,
    val root: @Composable (content: @Composable () -> Unit) -> Unit,
)