package com.michaelflisar.toolbox.coil.utils

import coil3.Bitmap
import org.jetbrains.skia.BlendMode
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.Font
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect

actual fun BitmapUtil.resizeFill(
    source: Bitmap,
    width: Int,
    height: Int,
): Bitmap {
    val sourceWidth = source.width
    val sourceHeight = source.height

    val xScale = width.toFloat() / sourceWidth
    val yScale = height.toFloat() / sourceHeight
    val scale = maxOf(xScale, yScale)

    val scaledWidth = (scale * sourceWidth).toInt()
    val scaledHeight = (scale * sourceHeight).toInt()

    val left = (width - scaledWidth) / 2
    val top = (height - scaledHeight) / 2

    val targetRect = Rect.makeLTRB(
        left.toFloat(),
        top.toFloat(),
        (left + scaledWidth).toFloat(),
        (top + scaledHeight).toFloat(),
    )

    return renderScaled(source, width, height, targetRect)
}

actual fun BitmapUtil.resizeFit(
    source: Bitmap,
    newHeight: Int,
    newWidth: Int,
): Bitmap {
    val sourceWidth = source.width
    val sourceHeight = source.height

    val xScale = newWidth.toFloat() / sourceWidth
    val yScale = newHeight.toFloat() / sourceHeight
    val scale = maxOf(xScale, yScale)

    val scaledWidth = scale * sourceWidth
    val scaledHeight = scale * sourceHeight

    val left = (newWidth - scaledWidth) / 2
    val top = (newHeight - scaledHeight) / 2

    val targetRect = Rect.makeLTRB(left, top, left + scaledWidth, top + scaledHeight)

    return renderScaled(source, newWidth, newHeight, targetRect)
}

private fun renderScaled(
    source: Bitmap,
    width: Int,
    height: Int,
    targetRect: Rect,
): Bitmap {
    val dest = Bitmap()
    check(dest.allocN32Pixels(width, height)) { "allocN32Pixels($width, $height) failed" }

    val canvas = Canvas(dest)
    val image = Image.makeFromBitmap(source)
    canvas.drawImageRect(image, targetRect)

    return dest
}

actual fun BitmapUtil.tint(
    source: Bitmap,
    color: Int,
): Bitmap {
    val dest = Bitmap()
    check(dest.allocN32Pixels(source.width, source.height)) { "allocN32Pixels failed" }

    val canvas = Canvas(dest)
    val image = Image.makeFromBitmap(source)
    canvas.drawImage(image, 0f, 0f)

    // Anwenden der Farbfilter durch Zeichnen mit ColorFilter
    val paint = Paint()
    paint.colorFilter = ColorFilter.makeBlend(color, BlendMode.SRC_IN)
    canvas.drawImage(image, 0f, 0f, paint)

    return dest
}

actual fun BitmapUtil.overlay(
    source: Bitmap,
    overlay: Bitmap,
    horizontalAlignment: BitmapUtil.HorizontalAlignment,
    verticalAlignment: BitmapUtil.VerticalAlignment,
): Bitmap {
    val dest = Bitmap()
    check(dest.allocN32Pixels(source.width, source.height)) { "allocN32Pixels failed" }

    val canvas = Canvas(dest)
    val sourceImage = Image.makeFromBitmap(source)
    val overlayImage = Image.makeFromBitmap(overlay)
    
    canvas.drawImage(sourceImage, 0f, 0f)

    val left = when (horizontalAlignment) {
        BitmapUtil.HorizontalAlignment.Left -> 0f
        BitmapUtil.HorizontalAlignment.Center -> (source.width - overlay.width) / 2f
        BitmapUtil.HorizontalAlignment.Right -> (source.width - overlay.width).toFloat()
    }
    val top = when (verticalAlignment) {
        BitmapUtil.VerticalAlignment.Top -> 0f
        BitmapUtil.VerticalAlignment.Center -> (source.height - overlay.height) / 2f
        BitmapUtil.VerticalAlignment.Bottom -> (source.height - overlay.height).toFloat()
    }

    canvas.drawImage(overlayImage, left, top)

    return dest
}

actual fun BitmapUtil.createTextBitmap(
    width: Int,
    height: Int,
    text: String,
    textSizeFactor: Float,
    textColor: Int,
    bold: Boolean,
): Bitmap {
    val dest = Bitmap()
    check(dest.allocN32Pixels(width, height)) { "allocN32Pixels failed" }

    val canvas = Canvas(dest)

    val fontSize = height * textSizeFactor
    val font = Font().apply {
        size = fontSize
    }

    val paint = Paint().apply {
        color = textColor
        isAntiAlias = true
    }

    val xPos = width / 2f
    val yPos = height / 2f + fontSize / 4f

    canvas.drawString(text, xPos, yPos, font, paint)

    return dest
}