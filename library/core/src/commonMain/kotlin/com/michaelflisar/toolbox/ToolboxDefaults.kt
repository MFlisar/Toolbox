package com.michaelflisar.toolbox

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

object ToolboxDefaults {

    val ITEM_SPACING = 8.dp
    val ITEM_SPACING_MINI = 2.dp
    val CONTENT_PADDING = 16.dp
    val CONTENT_PADDING_SMALL = 8.dp

    val DEFAULT_DIALOG_SIZE = DpSize(800.dp, 600.dp)
    val DEFAULT_SMALL_DIALOG_SIZE = DpSize(400.dp, 100.dp)
    val DEFAULT_COMPACT_DIALOG_SIZE = DpSize(400.dp, 200.dp)

    val COLOR_SCHEME = lightColorScheme(
        primary = Color(0xff1976d2), // blue
        primaryContainer = Color(0xffc1dcf7),
        secondary = Color(0xff00c853), // green
        secondaryContainer = Color(0xff91f5ba)
    )
}