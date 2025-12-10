package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.spacing


@Composable
fun MyVerticalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.width(MaterialTheme.spacing.default))
}