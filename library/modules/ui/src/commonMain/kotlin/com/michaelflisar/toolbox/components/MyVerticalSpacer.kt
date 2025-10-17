package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.michaelflisar.toolbox.LocalTheme

@Composable
fun MyVerticalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.width(LocalTheme.current.spacing.default))
}