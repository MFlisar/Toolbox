package com.michaelflisar.toolbox

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

object ToolboxDefaults {

    const val TEXT_EMPTY = "Leer"

    val DEFAULT_DIALOG_SIZE = DpSize(800.dp, 600.dp)
    val DEFAULT_DIALOG_SIZE_MEDIUM = DpSize(600.dp, 400.dp)
    val DEFAULT_DIALOG_SIZE_SMALL = DpSize(400.dp, 200.dp)

    private val SHAPE_SMALL = RoundedCornerShape(4.dp)
    val SHAPES = Shapes(
        extraSmall = SHAPE_SMALL,
        small = SHAPE_SMALL,
        medium = SHAPE_SMALL,
        large = SHAPE_SMALL,
        extraLarge = SHAPE_SMALL
    )
}