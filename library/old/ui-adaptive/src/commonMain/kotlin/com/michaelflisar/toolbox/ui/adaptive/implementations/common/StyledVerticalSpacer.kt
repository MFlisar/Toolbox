package com.michaelflisar.toolbox.ui.adaptive.implementations.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme

@Composable
fun StyledVerticalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.width(StyledTheme.spacing.medium))
}