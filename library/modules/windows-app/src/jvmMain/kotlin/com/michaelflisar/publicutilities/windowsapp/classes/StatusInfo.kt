package com.michaelflisar.publicutilities.windowsapp.classes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

data class StatusInfo(
    val info: String,
    val color: Color = Color.Unspecified,
    val fontWeight: FontWeight = FontWeight.Bold
)