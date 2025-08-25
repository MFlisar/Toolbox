package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.michaelflisar.toolbox.classes.LocalStyle

@Composable
fun MyHorizontalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(LocalStyle.current.spacingDefault))
}