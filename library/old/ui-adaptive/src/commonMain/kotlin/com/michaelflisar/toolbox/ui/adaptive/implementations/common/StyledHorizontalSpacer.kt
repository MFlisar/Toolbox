package com.michaelflisar.toolbox.ui.adaptive.implementations.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme

@Composable
fun StyledHorizontalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(StyledTheme.spacing.medium))
}