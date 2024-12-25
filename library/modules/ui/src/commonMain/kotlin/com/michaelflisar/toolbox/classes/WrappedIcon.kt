package com.michaelflisar.toolbox.classes

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

sealed class WrappedIcon {
    class ImageVector(val icon: androidx.compose.ui.graphics.vector.ImageVector) : WrappedIcon()
    class Painter(val painter: androidx.compose.ui.graphics.painter.Painter) : WrappedIcon()
}

fun ImageVector.asWrappedIcon() = WrappedIcon.ImageVector(this)
fun Painter.asWrappedIcon() = WrappedIcon.Painter(this)