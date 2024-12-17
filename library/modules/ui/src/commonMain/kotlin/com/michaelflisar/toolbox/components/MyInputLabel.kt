package com.michaelflisar.toolbox.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MyInputLabel(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    Text(modifier = modifier, text = label, fontWeight = FontWeight.Bold, color = color)
}