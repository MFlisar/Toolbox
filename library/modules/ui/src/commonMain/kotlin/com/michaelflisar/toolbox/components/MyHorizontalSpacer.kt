package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.spacing


@Composable
fun MyHorizontalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(MaterialTheme.spacing.default))
}