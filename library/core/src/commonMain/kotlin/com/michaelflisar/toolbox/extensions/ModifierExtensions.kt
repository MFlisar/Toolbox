package com.michaelflisar.toolbox.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.layout

fun Modifier.disabled(applyAlpha: Boolean = true, applyGreyscale: Boolean = true) =
    this.then(if (applyAlpha) alphaDisabled() else Modifier)
        .then(if (applyGreyscale) greyscale() else Modifier)

fun Modifier.alphaDisabled() = alpha(0.38f)
fun Modifier.greyscale(factor: Float = 1f) = then(GreyscaleModifier(factor))

private class GreyscaleModifier(val factor: Float) : DrawModifier {
    override fun ContentDrawScope.draw() {
        val saturationMatrix = ColorMatrix().apply { setToSaturation(1f - 1f * factor) }
        val saturationFilter = ColorFilter.colorMatrix(saturationMatrix)
        val paint = Paint().apply {
            colorFilter = saturationFilter
        }
        drawIntoCanvas {
            it.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
            drawContent()
            it.restore()
        }
    }
}

fun Modifier.vertical(
    degrees: Float = -90f
) =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }.rotate(degrees)


